import os
import json
import tkinter as tk
from functools import partial
from mq_communication import RabbitMq


class StackApp:
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
    A = None
    B = None

    def __init__(self, gui):
        self.gui = gui
        self.gui.title('Exemplul 1 cu Tkinter')

        self.gui.geometry("1050x300")

        self.stack_A_lbl = tk.Label(master=self.gui, text="Multimea A:")
        self.stack_B_lbl = tk.Label(master=self.gui, text="Multimea B:")
        self.stack_A = tk.Label(master=self.gui, text="[1, 2, 3]")
        self.stack_B = tk.Label(master=self.gui, text="[4, 5, 6]")

        self.regenerate_A_btn = tk.Button(master=self.gui,
                                          text="Generare multimea A",
                                          command=partial(self.send_request,
                                                          request='regenerate_A'))
        self.regenerate_B_btn = tk.Button(master=self.gui,
                                          text="Generare multimea B",
                                          command=partial(self.send_request,
                                                          request='regenerate_B'))
        self.compute_btn = tk.Button(master=self.gui,
                                     text="Calculare expresie",
                                     command=partial(self.send_request,
                                                     request='compute'))

        self.result = tk.Text(self.gui, width=50, height=10)

        # alignment on the grid
        self.stack_A_lbl.grid(row=0, column=0)
        self.stack_B_lbl.grid(row=1, column=0)
        self.stack_A.grid(row=0, column=1)
        self.stack_B.grid(row=1, column=1)
        self.regenerate_A_btn.grid(row=0, column=2)
        self.regenerate_B_btn.grid(row=1, column=2)
        self.compute_btn.grid(row=2, column=2)
        self.result.grid(row=2, column=0)

        self.rabbit_mq = RabbitMq(self)
        self.gui.mainloop()

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
    app = StackApp(root)
    root.mainloop()
