from abc import ABCMeta, abstractmethod

import requests
from requests import HTTPError

from mq_communication import RabbitMq


class AbstractAdapter(metaclass=ABCMeta):
    def __init__(self, type, ui):
        self.__type = type
        self.__next = None
        self._ui=ui

    def set_next(self, next):
        self.__next = next

    @abstractmethod
    def _search(self, format, attribute_name, attribute_value):
        pass

    def search(self, adaptor_type, format, attribute_name, attribute_value):
        if adaptor_type == self.__type:
            self._search(format, attribute_name, attribute_value)
        elif self.__next is not None:
            return self.__next.search(adaptor_type, format, attribute_name, attribute_value)
        else:
            return 'Unknown adaptor type'


class WebAdapter(AbstractAdapter):
    def __init__(self, ui):
        super().__init__('web', ui)

    def _search(self, format, attribute_name, attribute_value):
        url = 'http://localhost:8080'
        if not attribute_value:
            url += f'/print?format={format}'
        else:
            url += f'/find-and-print?attributeName={attribute_name}&attributeValue={attribute_value}&format={format}'
        try:
            response = requests.get(url).content.decode()
            self._ui.result.setText(response)
        except HTTPError as http_err:
            self._ui.result.setText('HTTP error occurred: {}'.format(http_err))
        except Exception as err:
            self._ui.result.setText('Other error occurred: {}'.format(err))


class RabbitMqAdaptor(AbstractAdapter):
    def __init__(self, ui):
        super().__init__('rabbit_mq', ui)
        self.__rabbit_mq = RabbitMq(ui)

    def _search(self, format, attribute_name, attribute_value):
        request = '{'
        if not attribute_value:
            request += '"function":"print",'
        else:
            request += '"function":"find",'
            request += f'"attribute":"{attribute_name}",'
            request += f'"value":"{attribute_value}",'
        request += f'"format":"{format}"'
        request += '}'
        self.__rabbit_mq.send_message(request)
        self.__rabbit_mq.receive_message()


class ChainFactory:
    @staticmethod
    def create_chain(ui):
        web = WebAdapter(ui)
        rb = RabbitMqAdaptor(ui)
        web.set_next(rb)
        return web