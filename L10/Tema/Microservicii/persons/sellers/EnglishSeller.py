from persons.sellers.abstracts.AbstractSellerThatDoesntDecideAuctionResult import AbstractSellerThatDoesntDecideAuctionResult
from json import loads


class EnglishSeller(AbstractSellerThatDoesntDecideAuctionResult):
    def __init__(self, start_bid_topic, result_topic, *args):
        min_bid = f'{args[0]}'.encode()
        start_informations = [('bid', min_bid)]
        super().__init__(start_informations, start_bid_topic, result_topic)

    def _process_result(self, result):
        for header in result.headers:
            if header[0] == 'winner':
                winner = header[1].decode()
            elif header[0] == 'bid':
                bid = header[1].decode()
        return f'Castigator: {winner}, pret: {bid}'
