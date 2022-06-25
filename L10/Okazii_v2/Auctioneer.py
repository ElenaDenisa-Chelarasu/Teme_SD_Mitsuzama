from kafka import KafkaConsumer, KafkaProducer


class Auctioneer:
    def __init__(self, bids_topic, notify_message_processor_topic):
        super().__init__()
        self.bids_topic = bids_topic
        self.notify_processor_topic = notify_message_processor_topic

        # consumatorul pentru ofertele de la licitatie
        self.bids_consumer = KafkaConsumer(
            self.bids_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
            group_id="auctioneers",
            consumer_timeout_ms=15_000  # timeout de 15 secunde
        )

        # producatorul pentru notificarea procesorului de mesaje
        self.notify_processor_producer = KafkaProducer()

    def receive_bids(self):
        # se preiau toate ofertele din topicul bids_topic
        print("Astept oferte pentru licitatie...")
        for msg in self.bids_consumer:
            for header in msg.headers:
                if header[0] == "identity":
                    identity = str(header[1], encoding="utf-8")
                elif header[0] == "amount":
                    bid_amount = int.from_bytes(header[1], 'big')

            print("{} a licitat {}".format(identity, bid_amount))

        # bids_consumer genereaza exceptia StopIteration atunci cand se atinge timeout-ul de 10 secunde
        # => licitatia se incheie dupa ce timp de 15 secunde nu s-a primit nicio oferta
        self.finish_auction()

    def finish_auction(self):
        print("Licitatia s-a incheiat!")
        self.bids_consumer.close()

        # se notifica MessageProcessor ca poate incepe procesarea mesajelor
        auction_finished_message = bytearray("incheiat", encoding="utf-8")
        self.notify_processor_producer.send(topic=self.notify_processor_topic, value=auction_finished_message)
        self.notify_processor_producer.flush()
        self.notify_processor_producer.close()

    def run(self):
        self.receive_bids()


if __name__ == '__main__':
    auctioneer = Auctioneer(
        bids_topic="topic_oferte",
        notify_message_processor_topic="topic_notificare_procesor_mesaje"
    )
    auctioneer.run()
