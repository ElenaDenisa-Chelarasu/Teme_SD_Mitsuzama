import threading
import time
from queue import Queue

import pika

# 5678
from PyQt5.QtCore import QObject
from PyQt5.QtWidgets import QTextEdit


class RabbitMq:
    def __init__(self, ui):
        super().__init__()
        self.config = {
            'host': '0.0.0.0',
            'port': 5672,
            'username': 'student',
            'password': 'student',
            'exchange': 'libraryapp.direct',
            'routing_key': 'libraryapp.routingkey1',
            'queue': 'libraryapp.queue'
        }
        self.ui = ui
        self.consumer_tag = None
        self.channel = None

        credentials = pika.PlainCredentials(self.config['username'], self.config['password'])
        self.parameters = pika.ConnectionParameters(host=self.config['host'], port=self.config['port'],
                                                    credentials=credentials)
        # self.parameters = pika.URLParameters(f"amqp://{self.config['username']}:{self.config['password']}@{self.config['host']}:{self.config['port']}/%2F")
        # self.parameters = pika.ConnectionParameters(host=self.config['host'])
        # self.parameters = (pika.ConnectionParameters(host=self.config['host']),
        #        pika.ConnectionParameters(port=self.config['port']),
        #        pika.ConnectionParameters(credentials=credentials))
        self.connection = pika.SelectConnection(parameters=self.parameters,
                                                on_open_callback=self.on_connected,
                                                on_close_callback=lambda a, b: print('Connection closed'))

    def on_connected(self, connection):
        print('Connection opened!')
        connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        print('Channel opened!')
        self.channel = channel
        self.start_consuming()

    def send_message(self, message):
        self.channel.basic_publish(exchange=self.config['exchange'],
                                   routing_key=self.config['routing_key'], body=message)

    def start_consuming(self):
        self.consumer_tag = self.channel.basic_consume(on_message_callback=self.on_message, queue=self.config['queue'],
                                                       auto_ack=True)

    def on_message(self, channel, method, properties, body):
        body = body.decode('utf-8')
        print(body)
        #time.sleep(10)
        self.ui.q.put(body)

    def run(self) -> None:
        threading.Thread(target=self.__run).start()

    def __run(self):
        try:
            self.connection.ioloop.start()
        except KeyboardInterrupt:
            self.stop()

    def stop(self):
        self.connection.close()
        self.connection.ioloop.stop()
