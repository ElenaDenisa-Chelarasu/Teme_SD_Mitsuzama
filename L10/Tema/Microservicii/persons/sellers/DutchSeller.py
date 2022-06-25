from persons.sellers.abstracts.AbstractSellerThatDoesntDecideAuctionResult import \
    AbstractSellerThatDoesntDecideAuctionResult
from functional import seq


class DutchSeller(AbstractSellerThatDoesntDecideAuctionResult):
    def __init__(self, start_bid_topic, result_topic, *args):
        start_informations = [
            ('bid', f'{args[0]}'.encode()),
            ('quantity', f'{args[1]}'.encode())
        ]
        super().__init__(start_informations, start_bid_topic, result_topic)

    def _process_result(self, result):
        winners = seq(result.headers)\
            .filter(lambda it: it[0] == 'winner')\
            .map(lambda it: it[1].decode().split(' -> '))\
            .map(lambda it: f'Castigator {it[0]}, cantitate {it[1]}, pret pe bucata {it[2]}')
        return 'Castigatori:\n' + '\n'.join(winners) + '\n'
