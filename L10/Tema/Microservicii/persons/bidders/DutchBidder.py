from random import randint
from persons.bidders.abstracts.AbstractBidder import AbstractBidder


class DutchBidder(AbstractBidder):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(start_bid_topic, bid_topic, result_topic)

    def __decide_to_bid(self):
        return randint(0, 9) < 7

    def _receive_bid(self):
        message = next(self._start_bid_consumer)
        for header in message.headers:
            if header[0] == 'bid':
                bid = header[1].decode()
                if bid == '-1':
                    return None, None
            elif header[0] == 'quantity':
                quantity = header[1].decode()
                if quantity == '0':
                    return None, None
        return bid, quantity


    def run(self):
        while True:
            bid, quantity = self._receive_bid()
            if bid is None or quantity is None:
                break
            quantity = int(quantity)
            if not self.__decide_to_bid():
                print(f'({self._my_id}) Ma retrag...')
                break
            quantity = f'{randint(1, quantity//4)}'
            headers = [
                ('bid', bid.encode()),
                ('quantity', quantity.encode())
            ]
            print(f'({self._my_id}) Licitez pentru {quantity} bucati la pretul de {bid}')
            self._bid(headers)
        self._receive_result()

    def _receive_result(self):
        print(f"({self._my_id}) Astept rezultatul licitatiei...")
        result = next(self._result_consumer)
        castigator = False
        # se verifica identitatea castigatorului
        for header in result.headers:
            if header[0] == 'winner':
                informations = header[1].decode().split(' -> ')
                if informations[0] == f'{self._my_id}':
                    print(
                        f'({self._my_id}) Am castigat {informations[1]} bucati platind {informations[2]} pe bucata.')
                    castigator = True
        if not castigator:
            print(f'({self._my_id}) Am pierdut...')
        self._result_consumer.close()