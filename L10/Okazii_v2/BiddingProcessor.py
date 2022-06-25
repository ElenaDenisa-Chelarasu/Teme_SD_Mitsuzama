from kafka import KafkaConsumer, KafkaProducer


class BiddingProcessor:
    def __init__(self, processed_bids_topic, result_topic):
        super().__init__()
        self.processed_bids_topic = processed_bids_topic
        self.result_topic = result_topic

        # consumatorul pentru ofertele procesate
        self.processed_bids_consumer = KafkaConsumer(
            self.processed_bids_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
            consumer_timeout_ms=3000
        )

        # producatorul pentru trimiterea rezultatului licitatiei
        self.result_producer = KafkaProducer()

    def get_processed_bids(self):
        # se preiau toate ofertele procesate din topicul processed_bids_topic
        print("Astept ofertele procesate de MessageProcessor...")

        # ofertele se stocheaza sub forma de perechi <PRET_LICITAT, MESAJ_OFERTA>
        bids = dict()
        no_bids_available = True

        while no_bids_available:
            for msg in self.processed_bids_consumer:
                for header in msg.headers:
                    if header[0] == "amount":
                        bid_amount = int.from_bytes(header[1], 'big')
                bids[bid_amount] = msg

            # daca inca nu exista oferte, se asteapta in continuare
            if len(bids) != 0:
                no_bids_available = False


        self.processed_bids_consumer.close()
        self.decide_auction_winner(bids)

    def decide_auction_winner(self, bids):
        print("Procesez ofertele...")

        if len(bids) == 0:
            print("Nu exista nicio oferta de procesat.")
            return

        # sortare dupa oferte, descrescator
        sorted_bids = sorted(bids.keys(), reverse=True)

        # castigatorul este ofertantul care a oferit pretul cel mai mare
        winner = bids[sorted_bids[0]]

        for header in winner.headers:
            if header[0] == "identity":
                winner_identity = str(header[1], encoding="utf-8")

        print("Castigatorul este:")
        print("\t{} - pret licitat: {}".format(winner_identity, sorted_bids[0]))

        # se trimite rezultatul licitatiei pentru ca entitatile Bidder sa il preia din topicul corespunzator
        self.result_producer.send(topic=self.result_topic, value=winner.value, headers=winner.headers)
        self.result_producer.flush()
        self.result_producer.close()

    def run(self):
        self.get_processed_bids()


if __name__ == '__main__':
    bidding_processor = BiddingProcessor(
        processed_bids_topic="topic_oferte_procesate",
        result_topic="topic_rezultat"
    )
    bidding_processor.run()
