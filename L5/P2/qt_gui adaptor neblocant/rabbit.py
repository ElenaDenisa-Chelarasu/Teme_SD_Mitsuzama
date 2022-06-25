import pika
from retry import retry
from threading import Thread
import queue


class RabbitMq:
    config = {
        'host': '0.0.0.0',
        'port': 5672,
        'username': 'student',
        'password': 'student',
        'exchange': 'libraryapp.direct',
        'routing_key': 'libraryapp.routingKey1',
        'queue': 'libraryapp.queue'
    }

    parameters = pika.URLParameters(f"amqp://{config['username']}:{config['password']}@{config['host']}:{config['port']}/%2F")

    def __init__(self, ui):
        self.ui = ui
        self.q = queue.Queue(2)

    def on_received_message(self, blocking_channel, deliver, properties, message):
        result = message.decode('utf-8')
        blocking_channel.basic_ack(deliver.delivery_tag)

        try:
            self.q.put(result)
        except Exception as e:
            print(e)
        finally:
            blocking_channel.stop_consuming()

    @retry(pika.exceptions.AMQPConnectionError, delay=5, jitter=(1, 3))
    def receive_message(self):
        print("received")
        mr = MessageReceiver(self.on_received_message)
        Thread(target=mr.run, args=(self.q,)).start()

        message = self.q.get()
        if message:
            self.ui.set_response(message)
            
    def send_message(self, message):
        ms = MessageSender(message)
        Thread(target=ms.run).start()
 


class MessageReceiver:
    config = {
        'host': '0.0.0.0',
        'port': 5672,
        'username': 'student',
        'password': 'student',
        'exchange': 'libraryapp.direct',
        'routing_key': 'libraryapp.routingKey1',
        'queue': 'libraryapp.queue'
    }

    parameters = pika.URLParameters(f"amqp://{config['username']}:{config['password']}@{config['host']}:{config['port']}/%2F")

    def __init__(self, received_message_callback):
        self.received_message = received_message_callback
        self.channel = None
        self.q = None
        self.connection = pika.SelectConnection(parameters=self.parameters,
                                                on_open_callback=self.on_connection_open,
                                                on_open_error_callback=self.on_connection_error)


    def on_connection_open(self, connection):
        self.connection = connection
        self.connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        self.channel = channel
        self.channel.basic_consume(self.config["queue"], self.received_message)

        # self.connection.close()

    def on_connection_error(self, *args):
        print(args)

    def run(self, q: queue.Queue):
        self.q = q
        try:
            self.connection.ioloop.start()
        except Exception as ex:
            print(ex)
            self.connection.close()
            self.connection.ioloop.start()


class MessageSender:
    config = {
        'host': '0.0.0.0',
        'port': 5672,
        'username': 'student',
        'password': 'student',
        'exchange': 'libraryapp.direct',
        'routing_key': 'libraryapp.routingKey1',
        'queue': 'libraryapp.queue'
    }

    parameters = pika.URLParameters(f"amqp://{config['username']}:{config['password']}@{config['host']}:{config['port']}/%2F")

    def __init__(self, message):
        self.message = message
        self.channel = None
        self.connection = pika.SelectConnection(parameters=self.parameters,
                                                on_open_callback=self.on_connection_open,
                                                on_open_error_callback=self.on_connection_error)

    def on_connection_open(self, connection):
        self.connection = connection
        self.connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        self.channel = channel
        self.clear_queue(channel)
        self.channel.basic_publish(exchange=self.config["exchange"],
                                  routing_key=self.config["routing_key"],
                                  body=self.message)
        print("Message sent")

        self.connection.close()

    def clear_queue(self, channel):
        channel.queue_purge(self.config['queue'])

    def on_connection_error(self, *args):
        print(args)

    def run(self):
        try:
            self.connection.ioloop.start()
        except Exception as ex:
            print(ex)
            self.connection.close()
            self.connection.ioloop.start()
