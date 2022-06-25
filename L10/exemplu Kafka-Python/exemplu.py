from kafka import KafkaConsumer
from kafka import KafkaProducer
import threading


class Consumer(threading.Thread):
    def __init__(self, topic):
        super().__init__()
        self.topic = topic

    def run(self) -> None:
        consumer = KafkaConsumer(self.topic, auto_offset_reset='earliest')
        # topicul va fi creat automat, daca nu exista deja

        # thread-ul consumator primeste mesajele din topic
        for msg in consumer:
            print("Am consumat mesajul: " + str(msg.value, encoding="utf-8"))


class Producer(threading.Thread):
    def __init__(self, topic):
        super().__init__()
        self.topic = topic

    def run(self) -> None:
        producer = KafkaProducer()
        for i in range(10):
            message = 'mesaj {}'.format(i)

            # thread-ul producator trimite mesaje catre un topic
            producer.send(topic=self.topic, value=bytearray(message, encoding="utf-8"))
            print("Am produs mesajul: {}".format(message))

        # metoda flush() asigura trimiterea batch-ului de mesaje produse
        producer.flush()


if __name__ == '__main__':
    # se creeaza 2 thread-uri: unul producator de mesaje si celalalt consumator
    producer_thread = Producer("topic_exemplu_python")
    consumer_thread = Consumer("topic_exemplu_python")

    producer_thread.start()
    consumer_thread.start()

    producer_thread.join()
    consumer_thread.join()
