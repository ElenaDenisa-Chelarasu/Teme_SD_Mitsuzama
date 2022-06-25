import os
import json
import tkinter as tk
import pygubu
from functools import partial
from mq_communication import RabbitMq


class StackApp:
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
    A = None
    B = None

    def __init__(self, master):
        self.master = master
        # 1: Create a builder
        self.builder = builder = pygubu.Builder()

        # 2: Load an ui file
        ui_path = os.path.join(self.ROOT_DIR, 'exemplul_1_v2.ui')
        builder.add_from_file(ui_path)

        # 3: Create the widget using a master as parent
        self.stack_app_window = builder.get_object('stack_app_window', master)

        self.stack_A = self.builder.get_object('stack_A')
        self.stack_B = self.builder.get_object('stack_B')
        self.result = self.builder.get_object('result')
        self.regenerate_A_btn = self.builder.get_object('regenerate_A_btn')
        self.regenerate_B_btn = self.builder.get_object('regenerate_B_btn')
        self.compute_btn = self.builder.get_object('compute_btn')
        self.regenerate_A_btn['command'] = partial(self.send_request,
                                                   request='regenerate_A')
        self.regenerate_B_btn['command'] = partial(self.send_request,
                                                   request='regenerate_B')
        self.compute_btn['command'] = partial(self.send_request,
                                              request='compute')
        builder.connect_callbacks(self)
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
        current_result = self.result.get("1.0", tk.END).split('\n')
        current_result[0] = 'A: ' + self.A
        self.stack_A['text'] = self.A
        self.result.delete("1.0", tk.END)
        self.result.insert(tk.END, '\n'.join(current_result))

    def regenerate_B(self, response):
        self.B = response
        current_result = self.result.get("1.0", tk.END).split('\n')
        if len(current_result) == 1:
            current_result.append('B: ' + self.B)
        else:
            current_result[1] = 'B: ' + self.B
        self.stack_B['text'] = self.B
        self.result.delete("1.0", tk.END)
        self.result.insert(tk.END, '\n'.join(current_result))

    def compute(self, response):
        dict_response = json.loads(response)
        result = ''
        for key in dict_response:
            result += '{}: {}\n'.format(key, dict_response[key])
        self.stack_A['text'] = dict_response['A']
        self.stack_B['text'] = dict_response['B']
        self.result.delete("1.0", tk.END)
        self.result.insert(tk.END, result)


if __name__ == '__main__':
    root = tk.Tk()
    root.title('Exemplul 1 cu Tkinter')
    app = StackApp(root)
    root.mainloop()
