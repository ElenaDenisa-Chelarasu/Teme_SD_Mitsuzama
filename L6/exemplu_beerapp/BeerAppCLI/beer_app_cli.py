import pika
from retry import retry


class RabbitMq:
    config = {
        'host': '0.0.0.0',
        'port': 5678,
        'username': 'student',
        'password': 'student',
        'exchange': 'beerapp.direct',
        'routing_key': 'beerapp.routingkey1',
        'queue': 'beerapp.queue'
    }
    credentials = pika.PlainCredentials(config['username'], config['password'])
    parameters = (pika.ConnectionParameters(host=config['host']),
                  pika.ConnectionParameters(port=config['port']),
                  pika.ConnectionParameters(credentials=credentials))

    def on_received_message(self, blocking_channel, deliver, properties,
                            message):
        result = message.decode('utf-8')
        blocking_channel.confirm_delivery()
        try:
            print(result)
        except Exception:
            print("wrong data format")
        finally:
            blocking_channel.stop_consuming()

    @retry(pika.exceptions.AMQPConnectionError, delay=5, jitter=(1, 3))
    def receive_message(self):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                channel.basic_consume(self.config['queue'],
                                      self.on_received_message)
                try:
                    channel.start_consuming()
                # Don't recover connections closed by server
                except pika.exceptions.ConnectionClosedByBroker:
                    print("Connection closed by broker.")
                # Don't recover on channel errors
                except pika.exceptions.AMQPChannelError:
                    print("AMQP Channel Error")
                # Don't recover from KeyboardInterrupt
                except KeyboardInterrupt:
                    print("Application closed.")

    def send_message(self, message):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                self.clear_queue(channel)
                channel.basic_publish(exchange=self.config['exchange'],
                                      routing_key=self.config['routing_key'],
                                      body=message)

    def clear_queue(self, channel):
        channel.queue_purge(self.config['queue'])


def print_menu():
    print('0 --> Exit program')
    print('1 --> addBeer')
    print('2 --> getBeers')
    print('3 --> getBeerByName')
    print('4 --> getBeerByPrice')
    print('5 --> updateBeer')
    print('6 --> deleteBeer')
    return input("Option=")


if __name__ == '__main__':
    rabbit_mq = RabbitMq()
    rabbit_mq.send_message("createBeerTable~")
    while True:
        option = print_menu()
        if option == '0':
            break
        elif option == '1':
            name = input("Beer name: ")
            price = float(input("Beer price: "))
            rabbit_mq.send_message("addBeer~id=-1;name={};price={}".format(name, price))
        elif option == '2':
            rabbit_mq.send_message("getBeers~")
            rabbit_mq.receive_message()
        elif option == '3':
            name = input("Beer name: ")
            rabbit_mq.send_message("getBeerByName~name={}".format(name))
            rabbit_mq.receive_message()
        elif option == '4':
            price = float(input("Beer price: "))
            rabbit_mq.send_message("getBeerByPrice~price={}".format(price))
            rabbit_mq.receive_message()
        elif option == '5':
            id = int(input("Beer ID: "))
            name = input("Beer name: ")
            price = float(input("Beer price: "))
            rabbit_mq.send_message("updateBeer~id={};name={};price={}".format(id, name, price))
            rabbit_mq.receive_message()
        elif option == '6':
            name = input("Beer name: ")
            rabbit_mq.send_message("deleteBeer~name={}".format(name))
        else:
            print("Invalid option")
