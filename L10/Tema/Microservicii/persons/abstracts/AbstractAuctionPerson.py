from abc import ABCMeta, abstractmethod

from kafka import KafkaProducer


class AbstractAuctionPerson(metaclass=ABCMeta):
    def __init__(self, result_topic):
        self._result_topic = result_topic

    def _send_message(self, topic, headers, value):
        producer = KafkaProducer()
        producer.send(
            topic,
            headers=headers,
            value=value
        )
        producer.flush()
        producer.close()

    @abstractmethod
    def run(self) -> None:
        pass
