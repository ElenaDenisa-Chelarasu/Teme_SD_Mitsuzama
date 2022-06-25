import json
import os
import queue
import sys
import threading
from queue import Queue

import pika as pika
from PyQt5.QtWidgets import QWidget, QApplication, QFileDialog, QMessageBox, QDialog, QFormLayout, QLabel, QLineEdit, \
    QDialogButtonBox
from PyQt5 import QtCore
from PyQt5.uic import loadUi
from retry import retry

from mq_communication import RabbitMq


def debug_trace(ui=None):
    from pdb import set_trace
    QtCore.pyqtRemoveInputHook()
    set_trace()
    # QtCore.pyqtRestoreInputHook()


class Book:
    def __init__(self, nume, autor, editura, text):
        self.nume = nume
        self.autor = autor
        self.editura = editura
        self.text = text


class LibraryApp(QWidget):
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

    def __init__(self):
        super(LibraryApp, self).__init__()
        ui_path = os.path.join(self.ROOT_DIR, 'exemplul_2.ui')
        loadUi(ui_path, self)
        self.search_btn.clicked.connect(self.search)
        self.save_as_file_btn.clicked.connect(self.save_as_file)
        self.add_book_btn.clicked.connect(self.add_book)
        self.q=Queue()
        self.rabbit_mq=RabbitMq(self)
        self.rabbit_mq.run()
        self.__result_format = None
        #threading.Thread(target=self.set_response).start()

#Sincron: fara thread, retry, cu set_response dupa send
#Asincron: cu thread, retry, fara set_response dupa send

    #@retry(Exception, tries=-1, delay=5, jitter=(1, 3))
    def set_response(self):
        message=self.q.get()
        self.result.setText(message)

    def send_request(self, request):
        self.rabbit_mq.send_message(message=request)
        self.set_response()

    def add_book(self):
        dialog = BookInputDialog(self)
        if dialog.exec():
            book = dialog.get_book()
            request = '{"function":"add","format":"json",'
            serialized_book = json.dumps(book.__dict__)
            request += f'"book":{serialized_book}' + '}'
            self.send_request(request)
            self.__result_format = 'raw'

    def search(self):
        search_string = self.search_bar.text()
        request = '{'
        if not search_string:
            request += '"function":"print",'
        else:
            request += '"function":"find",'
            if self.author_rb.isChecked():
                request += '"attribute":"author",'
            elif self.title_rb.isChecked():
                request += '"attribute":"title",'
            else:
                request += '"attribute":"publisher",'
            request += f'"value":"{search_string}",'
        self.__result_format = self.__get_selected_format()
        request += f'"format":"{self.__result_format}"' + '}'
        self.send_request(request)

    def __get_selected_format(self):
        if self.json_rb.isChecked():
            return 'json'
        elif self.html_rb.isChecked():
            return 'html'
        elif self.xml_rb.isChecked():
            return 'xml'
        else:
            return 'raw'

    def save_as_file(self):
        if self.__result_format is None:
            QMessageBox.critical(self, 'Eroare!', 'Nu ati realizat nici o cautare pana acum!')
            return
        format = self.__get_selected_format()
        if self.__result_format != format:
            QMessageBox.critical(self, 'Eroare!',
                                 'Rezultatul cautarii este in alt format decat cel in care se doreste a fi facuta salvarea!')
            return
        if format == 'raw':
            format = 'txt'
        options = QFileDialog.Options()
        options |= QFileDialog.DontUseNativeDialog
        file_path = str(
            QFileDialog.getSaveFileName(self,
                                        'Salvare fisier', '', f'*.{format}',
                                        options=options))
        if file_path:
            file_path = file_path.split("'")[1]
            if not file_path.endswith(f'.{format}'):
                file_path += f'.{format}'
            try:
                with open(file_path, 'w') as fp:
                    if file_path.endswith(".html"):
                        fp.write(self.result.toHtml())
                    else:
                        fp.write(self.result.toPlainText())
            except Exception as e:
                print(e)
                QMessageBox.warning(self, 'Exemplul 2',
                                    'Nu s-a putut salva fisierul')

    def stop(self):
        self.rabbit_mq.stop()


class BookInputDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)

        self.__nume_input_box = QLineEdit(self)
        self.__autor_input_box = QLineEdit(self)
        self.__editura_input_box = QLineEdit(self)
        self.__text_input_box = QLineEdit(self)
        self.__book = None

        button_box = QDialogButtonBox(QDialogButtonBox.Ok | QDialogButtonBox.Cancel, self)
        layout = QFormLayout(self)

        layout.addRow("Nume: ", self.__nume_input_box)
        layout.addRow("Autor: ", self.__autor_input_box)
        layout.addRow("Editura: ", self.__editura_input_box)
        layout.addRow("Text: ", self.__text_input_box)
        layout.addWidget(button_box)

        button_box.accepted.connect(self.__validate_inputs)
        button_box.rejected.connect(self.reject)

    def __validate_inputs(self):
        d = {'nume': self.__nume_input_box.text(), 'autor': self.__autor_input_box.text(),
             'editura': self.__editura_input_box.text(), 'text': self.__text_input_box.text()}
        for value in d.values():
            if value == '':
                QMessageBox.critical(self, 'Eroare!', 'Unul din campuri este gol!')
                return
        self.__book = Book(**d)
        self.accept()

    def get_book(self) -> Book:
        return self.__book



if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = LibraryApp()
    try:
        window.show()
        code=app.exec_()
        sys.exit(code)
    finally:
        window.stop()

