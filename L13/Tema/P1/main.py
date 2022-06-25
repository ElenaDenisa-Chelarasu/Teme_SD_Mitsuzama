from threading import Thread
from kafka import KafkaProducer, KafkaConsumer
from pynput.mouse import Controller
from time import sleep

class MousePositionCapturer:
    def __init__(self):
        self.__mouse = Controller()

    def get_mouse_position(self):
        return self.__mouse.position

class MousePositionProducer:
    def __init__(self, topic):
        self.__mouse = MousePositionCapturer()
        self.__topic = topic

    def __send_position(self, producer, position):
        producer.send(
            self.__topic,
            value = f'{position}'.encode()
        )
        producer.flush()


    def run(self):
        producer = KafkaProducer()
        for _ in range(100):
            position = self.__mouse.get_mouse_position()
            self.__send_position(producer, position)
            sleep(0.1)
        producer.close()

class MousePositionConsumer:
    def __init__(self, topic):
        self.__consumer = KafkaConsumer(topic, auto_offset_reset='earliest', consumer_timeout_ms=1000)

    def run(self):
        for message in self.__consumer:
            print(message.value.decode())

if __name__ == '__main__':
    topic = 'lab13temap1'
    l = [
        MousePositionProducer(topic),
        MousePositionConsumer(topic),
    ]
    threads = [Thread(target=it.run) for it in l]
    for t in threads:
        t.start()
    for t in threads:
        t.join()
