from abc import ABCMeta, abstractmethod
from kafka import KafkaConsumer
from multiprocessing import Queue
from persons.sellers.abstracts.AbstractSeller import AbstractSeller


class AbstractSellerThatDoesntDecideAuctionResult(AbstractSeller, metaclass=ABCMeta):
    def __init__(self, start_informations, start_bid_topic, result_topic):
        super().__init__(start_informations, start_bid_topic, result_topic)
        self.__result_consumer = KafkaConsumer(
            self._result_topic,
            auto_offset_reset='earliest'
        )

    @abstractmethod
    def _process_result(self, result) -> str:
        pass

    def _receive_result(self):
        result = next(self.__result_consumer)
        processed_result = self._process_result(result)
        print(f'(Seller) {processed_result}')

    def run(self) -> None:
        super().run()
        self._receive_result()
