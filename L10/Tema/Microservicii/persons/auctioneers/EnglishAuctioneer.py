from kafka import KafkaProducer

from persons.auctioneers.abstracts.AbstractAuctioneerThatDecidesAuctionResult import AbstractAuctioneerThatDecidesAuctionResult
from functional import seq


class EnglishAuctioneer(AbstractAuctioneerThatDecidesAuctionResult):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(start_bid_topic, bid_topic, result_topic)
        self.__current_max_bid = None

    def _process_bids(self):
        max_bid = seq(self._bids.items()) \
            .max_by(lambda it: int(it[1]))
        if self.__current_max_bid is not None and max_bid < self.__current_max_bid:
            raise Exception('Bid mai mic decat bidul minim!')
        self.__current_max_bid = max_bid

    def _auction_is_finished(self):
        return len(self._bids) == 0

    def _process_single_bid(self, msg):
        for header in msg.headers:
            if header[0] == "identity":
                identity = header[1].decode()
            elif header[0] == "amount":
                bid_amount = header[1].decode()
        return identity, bid_amount

    def _create_result_headers(self):
        if self.__current_max_bid is not None:
            return [
                ('winner', self.__current_max_bid[0].encode()),
                ('bid', self.__current_max_bid[1].encode())
            ]
        else:
            return [
                ('winner', b'None'),
                ('bid', b'-1')
            ]

    def run(self):
        self._receive_start_informations()
        while True:
            self._receive_bids_for_x_seconds()
            if self._auction_is_finished():
                self._send_message(
                    self._start_bid_topic,
                    headers=[('bid', '-1'.encode())],
                    value=b'finished'
                )
                break
            self._process_bids()
            self._send_message(
                self._start_bid_topic,
                headers=[('min_bid', self.__current_max_bid[1].encode())],
                value=b'start next bid round'
            )
        headers = self._create_result_headers()
        self._send_auction_result(headers)
