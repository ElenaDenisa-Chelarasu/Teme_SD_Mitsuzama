from abc import ABCMeta, abstractmethod

from kafka import KafkaConsumer

from persons.abstracts.AbstractAuctionPerson import AbstractAuctionPerson


class AbstractAuctioneer(AbstractAuctionPerson, metaclass=ABCMeta):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(result_topic)
        self._start_bid_topic = start_bid_topic
        self._bid_topic = bid_topic
        self._start_informations = None
        self._bids = None
        self.__bids_consumer = KafkaConsumer(
            self._bid_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
            consumer_timeout_ms=5_000  # timeout de 5 secunde
        )
        self.__bid_round = 1

    def _receive_start_informations(self):
        print('(Auctioneer) Primesc informatiile de start')
        consumer = KafkaConsumer(
            self._start_bid_topic,
            auto_offset_reset="earliest"  # mesajele se preiau de la cel mai vechi la cel mai recent
        )
        self._start_informations = next(consumer)
        consumer.close()
        print(f'(Auctioneer) Am primit informatiile de start: {self._start_informations}')

    @abstractmethod
    def run(self):
        pass

    @abstractmethod
    def _process_single_bid(self, msg):
        pass

    def _receive_bids_for_x_seconds(self):
        print(f'(Auctioneer) Primesc a {self.__bid_round}-a runda de bid-uri')
        self.__bid_round += 1

        self._bids = {}
        for msg in self.__bids_consumer:
            identity, bid_amount = self._process_single_bid(msg)
            self._bids[identity] = bid_amount
            print(f'(Auctioneer) Bid nou: {identity} -> {bid_amount}')


