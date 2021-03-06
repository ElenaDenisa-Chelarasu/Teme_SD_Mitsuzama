import sys
import os
import json
from functools import partial
from PyQt5 import QtWidgets
from mq_communication import RabbitMq
from generated_ui import Ui_stack_app_window


class StackApp(QtWidgets.QWidget, Ui_stack_app_window):
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
    A = None
    B = None

    def __init__(self, obj=None, *args, **kwargs):
        super(StackApp, self).__init__(*args, **kwargs)
        self.setupUi(self)
        self.regenerate_A_btn.clicked.connect(partial(self.send_request,
                                                      request='regenerate_A'))
        self.regenerate_B_btn.clicked.connect(partial(self.send_request,
                                                      request='regenerate_B'))
        self.compute_btn.clicked.connect(partial(self.send_request,
                                                 request='compute'))
        self.rabbit_mq = RabbitMq(self)

    def set_response(self, variable, response):
        if variable == 'A':
            self.regenerate_A(response)
        elif variable == 'B':
            self.regenerate_B(response)
        elif variable == 'compute':
            self.compute(response)

    def send_request(self, request):
        self.rabbit_mq.send_message(message=request)
        self.rabbit_mq.receive_message()

    def regenerate_A(self, response):
        self.A = response
        current_result = self.result.toPlainText().split('\n')
        current_result[0] = 'A: ' + self.A
        self.stack_A.setText(self.A)
        self.result.setText('\n'.join(current_result))

    def regenerate_B(self, response):
        self.B = response
        current_result = self.result.toPlainText().split('\n')
        if len(current_result) == 1:
            current_result.append('B: ' + self.B)
        else:
            current_result[1] = 'B: ' + self.B
        self.stack_B.setText(self.B)
        self.result.setText('\n'.join(current_result))

    def compute(self, response):
        dict_response = json.loads(response)
        result = ''
        for key in dict_response:
            result += '{}: {}\n'.format(key, dict_response[key])
        self.stack_A.setText(dict_response['A'])
        self.stack_B.setText(dict_response['B'])
        self.result.setText(result)


if __name__ == '__main__':
    app = QtWidgets.QApplication(sys.argv)
    window = StackApp()
    window.show()
    sys.exit(app.exec_())
