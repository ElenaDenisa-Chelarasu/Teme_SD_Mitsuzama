from random import randint

from persons.bidders.abstracts.AbstractBidder import AbstractBidder


class EnglishBidder(AbstractBidder):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(start_bid_topic, bid_topic, result_topic)

    def __decide_to_bid(self):
        return randint(0, 1) == 1

    def _receive_bid(self):
        message = next(self._start_bid_consumer)
        for header in message.headers:
            if header[0] == 'bid':
                result = header[1].decode()
                if result == '-1':
                    return None
                return result

    def run(self):
        while True:
            min_bid = self._receive_bid()
            if min_bid is not None:
                min_bid = int(min_bid)
            else:
                break
            if not self.__decide_to_bid():
                print(f'({self._my_id}) Ma retrag...')
                break
            bid = f'{randint(min_bid, int(min_bid*1.1))}'
            headers = [('amount', f'{bid}'.encode())]
            self._bid(headers)
        self._receive_result()
