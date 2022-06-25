from abc import ABCMeta, abstractmethod
from random import randint
from uuid import uuid4

from kafka import KafkaConsumer, KafkaProducer

from persons.abstracts.AbstractAuctionPerson import AbstractAuctionPerson


class AbstractBidder(AbstractAuctionPerson, metaclass=ABCMeta):
    def __init__(self, start_bid_topic, bid_topic, result_topic):
        super().__init__(result_topic)
        self._start_bid_topic = start_bid_topic
        self._bid_topic = bid_topic
        self._my_id = uuid4()  # se genereaza un identificator unic pentru ofertant
        self._start_bid_consumer = KafkaConsumer(
            self._start_bid_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
        )
        self._result_consumer = KafkaConsumer(
            self._result_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
        )

    @abstractmethod
    def _receive_bid(self):
        pass

    def _bid(self, headers):
        self._send_message(
            self._bid_topic,
            headers=[
                ('identity', f'{self._my_id}'.encode()),
                *headers
            ],
            value=b'licitez'
        )

    def _receive_result(self):
        print(f"({self._my_id}) Astept rezultatul licitatiei...")
        result = next(self._result_consumer)
        # se verifica identitatea castigatorului
        for header in result.headers:
            if header[0] == 'winner':
                identity = header[1].decode()
            elif header[0] == 'bid':
                bid = header[1].decode()
        if identity == f'{self._my_id}':
            print(f'({self._my_id}) Am castigat platind {bid} !')
        else:
            print(f'({self._my_id}) Am pierdut...')
        self._result_consumer.close()
