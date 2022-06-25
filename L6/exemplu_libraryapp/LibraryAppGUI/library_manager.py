import os
import sys
import qdarkstyle
from PyQt5.QtWidgets import QWidget, QApplication, QFileDialog, QMessageBox
from PyQt5 import QtCore
from PyQt5.uic import loadUi

from communication_adapters import ChainFactory


def debug_trace(ui=None):
    from pdb import set_trace
    QtCore.pyqtRemoveInputHook()
    set_trace()
    # QtCore.pyqtRestoreInputHook()


class LibraryApp(QWidget):
    ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

    def __init__(self, ):
        super(LibraryApp, self).__init__()
        ui_path = os.path.join(self.ROOT_DIR, 'library_manager.ui')
        loadUi(ui_path, self)
        self.search_btn.clicked.connect(self.search)
        self.save_as_file_btn.clicked.connect(self.save_as_file)
        self.__adapter = ChainFactory.create_chain(self)

    def search(self):
        attribute_value = self.search_bar.text()
        if self.author_rb.isChecked():
            attribute_name = 'author'
        elif self.title_rb.isChecked():
            attribute_name = 'title'
        else:
            attribute_name = 'publisher'
        if self.json_rb.isChecked():
            format = 'json'
        elif self.html_rb.isChecked():
            format = 'html'
        else:
            format = 'raw'
        if self.search_type.currentIndex() == 0:
            self.__adapter.search('web', format, attribute_name, attribute_value)
        else:
            self.__adapter.search('rabbit_mq', format, attribute_name, attribute_value)

    def save_as_file(self):
        options = QFileDialog.Options()
        options |= QFileDialog.DontUseNativeDialog
        file_path = str(
            QFileDialog.getSaveFileName(self,
                                        'Salvare fisier',
                                        options=options))
        if file_path:
            file_path = file_path.split("'")[1]
            if not file_path.endswith('.json') and not file_path.endswith(
                    '.html') and not file_path.endswith('.txt'):
                if self.json_rb.isChecked():
                    file_path += '.json'
                elif self.html_rb.isChecked():
                    file_path += '.html'
                else:
                    file_path += '.txt'
            try:
                with open(file_path, 'w') as fp:
                    if file_path.endswith(".html"):
                        fp.write(self.result.toHtml())
                    else:
                        fp.write(self.result.toPlainText())
            except Exception as e:
                print(e)
                QMessageBox.warning(self, 'Library Manager',
                                    'Nu s-a putut salva fisierul')


if __name__ == '__main__':
    app = QApplication(sys.argv)

    stylesheet = qdarkstyle.load_stylesheet_pyqt5()
    app.setStyleSheet(stylesheet)

    window = LibraryApp()
    window.show()
    sys.exit(app.exec_())
