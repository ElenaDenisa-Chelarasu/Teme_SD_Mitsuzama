from tkinter import *
from tkinter import ttk
import threading
import socket
import json


class GUI(Tk):
    HOST = "localhost"
    TEACHER_PORT = 1600

    def __init__(self):
        # elementul radacina al interfetei grafice
        super().__init__()
        self.title("Interactiune profesor-studenti")

        # la redimensionarea ferestrei, cadrele se extind pentru a prelua spatiul ramas
        self.columnconfigure(0, weight=1)
        self.rowconfigure(0, weight=1)

        # cadrul care incapsuleaza intregul continut
        self.content = ttk.Frame(self)

        # caseta text care afiseaza raspunsurile la intrebari
        self.response_widget = Text(self.content, height=10, width=50)

        # eticheta text din partea dreapta
        self.question_label = ttk.Label(self.content, text="Intrebare:")
        self.source_port_label = ttk.Label(self.content, text="Portul studentului:")
        self.destination_port_label = ttk.Label(self.content, text="ID-ul destinatie:")

        # caseta de introducere text cu care se preia intrebarea de la utilizator
        self.question = ttk.Entry(self.content, width=50)

        # butoanele din dreapta-jos
        self.ask = ttk.Button(self.content, text='Intreaba',
                              command=self.__ask_question)  # la apasare, se apeleaza functia ask_question
        self.exitbtn = ttk.Button(self.content, text='Iesi', command=self.destroy)  # la apasare, se iese din aplicatie

        # drop down list profesor/student
        self.message_source_field = StringVar(self.content)
        self.message_source_field.set('Profesor')
        self.message_source_dropdown_list = OptionMenu(self.content, self.message_source_field, 'Profesor', 'Student')

        # drop down list one-to-one/one-to-all
        self.one_to_x = StringVar(self.content)
        self.one_to_x.set('1to1')
        self.one_to_x_dropdown_list = OptionMenu(self.content, self.one_to_x, "1to1", "1toa")

        # drop down list raspuns privat/public
        self.response_type = StringVar(self.content)
        self.response_type.set('Public')
        self.response_type_dropdown_list = OptionMenu(self.content, self.response_type, 'Public', 'Privat')

        self.source_port = ttk.Entry(self.content, width=10)
        self.destination_port = ttk.Entry(self.content, width=10)

        # plasarea elementelor in layout-ul de tip grid
        self.content.grid(column=0, row=0)
        self.response_widget.grid(column=0, row=0, columnspan=3, rowspan=6)
        self.question_label.grid(column=3, row=0, columnspan=2)
        self.question.grid(column=3, row=1, columnspan=2)
        self.ask.grid(column=3, row=5)
        self.exitbtn.grid(column=4, row=5)
        self.message_source_dropdown_list.grid(column=3, row=0)
        self.source_port_label.grid(column=3, row=3)
        self.source_port.grid(column=4, row=3)
        self.one_to_x_dropdown_list.grid(column=5, row=3)
        self.destination_port_label.grid(column=3, row=4)
        self.destination_port.grid(column=4, row=4)
        self.response_type_dropdown_list.grid(column=5, row=4)

    def __resolve_question(self, question_text):
        # creare socket TCP
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        # incercare de conectare catre microserviciul Teacher
        try:
            if self.message_source_field.get() == 'Profesor':
                sock.connect((GUI.HOST, GUI.TEACHER_PORT))
            elif self.message_source_field.get() == 'Student':
                port = int(self.source_port.get())
                if port == self.TEACHER_PORT:
                    response_text = f'In campul portului studentului sursa a fost introdus portul profesorului, {self.TEACHER_PORT}...'
                    self.response_widget.insert(END, response_text)
                    return
                sock.connect((GUI.HOST, port))

            # transmitere intrebare - se deleaga intrebarea catre microserviciu
            sock.send(bytes(question_text + "\n", "utf-8"))

            # primire raspuns -> microserviciul comandat foloseste coregrafia de microservicii pentru a trimite raspunsul inapoi
            response_text = ''
            data = '??????'
            while data != '':
                data = str(sock.recv(1024), 'utf-8')
                response_text += data

        except ConnectionError:
            # in cazul unei erori de conexiune, se afiseaza un mesaj
            response_text = 'Eroare de conectare la microserviciul dorit (teacher sau student)!'
        except ValueError:
            response_text = 'Port-ul studentului nu a putut fi parsat!'

        # se adauga raspunsul primit in caseta text din interfata grafica
        self.response_widget.insert(END, response_text)

    def __ask_question(self):
        try:
            # preluare text intrebare de pe interfata grafica
            question_text = '{'
            question_text += f'"type":"intrebare",'
            question_text += f'"destinationType":"{self.one_to_x.get()}",'
            question_text += f'"responseType":"{self.response_type.get()}",'
            question_text += f'"destination":"{self.destination_port.get()}",'
            question_text += f'"content":{json.dumps(self.question.get())},'
            question_text += f'"source":"{self.source_port.get()}"'
            question_text = question_text + '}'

            # pornire thread separat pentru tratarea intrebarii respective
            # astfel, nu se blocheaza interfata grafica!
            threading.Thread(target=self.__resolve_question, args=(question_text,)).start()
        except ValueError:
            response_text = 'Port-ul destinatie nu a putut fi parsat!'
            self.response_widget.insert(END, response_text)


if __name__ == '__main__':
    gui = GUI()

    gui.mainloop()
