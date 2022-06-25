from datetime import datetime
from kafka import KafkaConsumer, KafkaProducer


class MessageProcessor:
    def __init__(self, bids_topic, notify_message_processor_topic, processed_bids_topic):
        super().__init__()
        self.bids_topic = bids_topic
        self.notify_message_processor_topic = notify_message_processor_topic
        self.processed_bids_topic = processed_bids_topic

        # consumatorul notificarii de la Auctioneer cum ca s-a terminat licitatia
        self.notify_message_processor_consumer = KafkaConsumer(
            self.notify_message_processor_topic,
            auto_offset_reset="earliest"  # mesajele se preiau de la cel mai vechi la cel mai recent
        )

        # consumatorul pentru ofertele de la licitatie
        self.bids_consumer = KafkaConsumer(
            self.bids_topic,
            auto_offset_reset="earliest",
            consumer_timeout_ms=1000
        )

        # producatorul pentru mesajele procesate
        self.processed_bids_producer = KafkaProducer()

        # ofertele se pun in dictionar, sub forma de perechi <IDENTITATE_OFERTANT, MESAJ_OFERTA>
        self.bids = dict()

    def get_and_process_messages(self):
        # se asteapta notificarea de la Auctioneer pentru incheierea licitatiei
        print("Astept notificare de la toate entitatile Auctioneer pentru incheierea licitatiei...")
        auction_end_message = next(self.notify_message_processor_consumer)

        # a ajuns prima notificare, se asteapta si celelalte notificari timp de maxim 15 secunde
        self.notify_message_processor_consumer.config["consumer_timeout_ms"] = 15_000
        for auction_end_message in self.notify_message_processor_consumer:
            pass
        self.notify_message_processor_consumer.close()

        if str(auction_end_message.value, encoding="utf-8") == "incheiat":
            # se preiau toate ofertele din topicul bids_topic si se proceseaza
            print("Licitatie incheiata. Procesez mesajele cu oferte...")

            for msg in self.bids_consumer:
                for header in msg.headers:
                    if header[0] == "identity":
                        identity = str(header[1], encoding="utf-8")

                # eliminare duplicate
                if identity not in self.bids.keys():
                    self.bids[identity] = msg

            self.bids_consumer.close()

            # sortare dupa timestamp
            sorted_bids = sorted(self.bids.values(), key=lambda bid: bid.timestamp)

            self.finish_processing(sorted_bids)

    def finish_processing(self, sorted_bids):
        print("Procesarea s-a incheiat! Trimit urmatoarele oferte:")
        for bid in sorted_bids:
            for header in bid.headers:
                if header[0] == "identity":
                    identity = str(header[1], encoding="utf-8")
                elif header[0] == "amount":
                    bid_amount = int.from_bytes(header[1], 'big')
            print("[{}] {} a licitat {}.".format(datetime.fromtimestamp(bid.timestamp / 1000), identity, bid_amount))

            # se stocheaza mesajele ordonate dupa timestamp si fara duplicate intr-un topic separat
            self.processed_bids_producer.send(topic=self.processed_bids_topic, value=bid.value, headers=bid.headers)

        self.processed_bids_producer.flush()
        self.processed_bids_producer.close()

    def run(self):
        self.get_and_process_messages()


if __name__ == '__main__':
    message_processor = MessageProcessor(
        bids_topic="topic_oferte",
        notify_message_processor_topic="topic_notificare_procesor_mesaje",
        processed_bids_topic="topic_oferte_procesate"
    )
    message_processor.run()
