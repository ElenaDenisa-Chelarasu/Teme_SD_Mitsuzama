from functional import seq

from persons.auctioneers.abstracts.AbstractAuctioneerThatDecidesAuctionResult import \
    AbstractAuctioneerThatDecidesAuctionResult


class DutchAuctioneer(AbstractAuctioneerThatDecidesAuctionResult):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(start_bid_topic, bid_topic, result_topic)
        self.__current_winners = []
        self.__remaining_quantity = None
        self.__current_max_bid = None
        self.__decrement_step = None

    def _receive_start_informations(self):
        super()._receive_start_informations()
        for header in self._start_informations.headers:
            if header[0] == 'quantity':
                self.__remaining_quantity = int(header[1].decode())
            elif header[0] == 'bid':
                self.__current_max_bid = int(header[1].decode())
                self.__decrement_step = self.__current_max_bid // 10

    def _process_single_bid(self, msg):
        for header in msg.headers:
            if header[0] == "identity":
                identity = header[1].decode()
            elif header[0] == "bid":
                bid_amount = header[1].decode()
            elif header[0] == 'quantity':
                quantity = header[1].decode()
        return identity, {'bid_amount': int(bid_amount), 'quantity': int(quantity)}

    def _process_bids(self):
        while len(self._bids) > 0:
            max_bid = seq(self._bids.items()) \
                .max_by(lambda it: it[1]['quantity'])
            quantity = min(max_bid[1]['quantity'], self.__remaining_quantity)
            self.__current_winners.append(f'{max_bid[0]} -> {quantity} -> {max_bid[1]["bid_amount"]}')
            self.__remaining_quantity -= quantity
            self._bids.pop(max_bid[0])
            if self.__remaining_quantity == 0:
                break

    def _create_result_headers(self):
        if len(self.__current_winners) > 0:
            return [('winner', it.encode()) for it in self.__current_winners]
        else:
            return [
                ('winner', b'None -> 0 -> -1'),
            ]

    def _auction_is_finished(self):
        return self.__remaining_quantity == 0 or self.__current_max_bid == 0

    def __end_auction(self):
        self._send_message(
            self._start_bid_topic,
            headers=[('bid', '-1'.encode()), ('quantity', '0'.encode())],
            value=b'finished'
        )

    def run(self):
        self._receive_start_informations()
        while True:
            self._receive_bids_for_x_seconds()
            if len(self._bids) == 0:
                self.__end_auction()
                break
            self.__current_max_bid -= self.__decrement_step
            self._process_bids()
            if self._auction_is_finished():
                self.__end_auction()
                break
            self._send_message(
                self._start_bid_topic,
                headers=[
                    ('bid', f'{self.__current_max_bid}'.encode()),
                    ('quantity', f'{self.__remaining_quantity}'.encode())
                ],
                value=b'start next bid round'
            )
        headers = self._create_result_headers()
        self._send_auction_result(headers)
