import multiprocessing

from ilock import ILock
from AuctionUtilities import create_topics
from persons.auctioneers.DutchAuctioneer import DutchAuctioneer
from persons.auctioneers.EnglishAuctioneer import EnglishAuctioneer
from persons.bidders.DutchBidder import DutchBidder
from persons.bidders.EnglishBidder import EnglishBidder
from persons.sellers.DutchSeller import DutchSeller
from persons.sellers.EnglishSeller import EnglishSeller
from sys import argv


# english bidders_number min_bid
# dutch bidders_number max_bid quantity
# english 10 1500
# dutch 10 2000 30
def create_persons(auction_type, start_bid_topic, bid_topic, result_topic, *args):
    supported_auctions = ['english', 'dutch']
    if auction_type not in supported_auctions:
        raise Exception(f'Tip de licitatie necunoscut: {auction_type}')
    bidders = {'english': EnglishBidder, 'dutch': DutchBidder}
    auctioneers = {'english': EnglishAuctioneer, 'dutch': DutchAuctioneer}
    sellers = {'english': EnglishSeller, 'dutch': DutchSeller}
    persons = [bidders[auction_type](start_bid_topic, bid_topic, result_topic) for _ in range(args[0])]
    persons.append(auctioneers[auction_type](start_bid_topic, bid_topic, result_topic))
    persons.append(sellers[auction_type](start_bid_topic, result_topic, *args[1:]))
    return persons


if __name__ == '__main__':
    with ILock('auction'):
        start_bid_topic, bid_topic, result_topic = create_topics('start_bid_topic', 'bid_topic', 'result_topic')

    auction_type = argv[1]
    args = [int(it) for it in argv[2:]]
    persons = create_persons(auction_type, start_bid_topic, bid_topic, result_topic, *args)
    processes = [multiprocessing.Process(target=person.run) for person in persons]

    for p in processes:
        p.start()

    for p in processes:
        p.join()
