from abc import ABCMeta
from multiprocessing import Queue
from kafka import KafkaProducer
from persons.abstracts.AbstractAuctionPerson import AbstractAuctionPerson


class AbstractSeller(AbstractAuctionPerson, metaclass=ABCMeta):
    def __init__(self, start_informations, start_bid_topic, result_topic):
        super().__init__(result_topic)
        self.__start_informations = start_informations
        self.__start_bid_topic = start_bid_topic

    def __send_start_informations(self):
        self._send_message(
            self.__start_bid_topic,
            headers=self.__start_informations,
            value=b'start informations'
        )

    def run(self) -> None:
        self.__send_start_informations()
