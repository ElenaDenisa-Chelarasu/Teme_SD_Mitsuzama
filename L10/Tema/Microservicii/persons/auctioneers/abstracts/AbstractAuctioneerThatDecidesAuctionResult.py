from abc import ABCMeta, abstractmethod

from kafka import KafkaProducer

from persons.auctioneers.abstracts.AbstractAuctioneer import AbstractAuctioneer


class AbstractAuctioneerThatDecidesAuctionResult(AbstractAuctioneer, metaclass=ABCMeta):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(start_bid_topic, bid_topic, result_topic)

    @abstractmethod
    def _process_bids(self):
        pass

    @abstractmethod
    def _auction_is_finished(self):
        pass

    @abstractmethod
    def _create_result_headers(self):
        pass

    def _send_auction_result(self, headers):
        self._send_message(
            self._result_topic,
            headers=headers,
            value=b'result'
        )

    