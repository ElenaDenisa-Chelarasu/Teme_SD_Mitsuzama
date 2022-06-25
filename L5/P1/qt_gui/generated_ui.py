# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'exemplul_1_v1.ui'
#
# Created by: PyQt5 UI code generator 5.12.3
#
# WARNING! All changes made in this file will be lost!


from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_stack_app_window(object):
    def setupUi(self, stack_app_window):
        stack_app_window.setObjectName("stack_app_window")
        stack_app_window.resize(791, 336)
        self.gridLayout = QtWidgets.QGridLayout(stack_app_window)
        self.gridLayout.setObjectName("gridLayout")
        self.horizontalLayout_2 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_2.setObjectName("horizontalLayout_2")
        self.stack_A_lbl = QtWidgets.QLabel(stack_app_window)
        self.stack_A_lbl.setObjectName("stack_A_lbl")
        self.horizontalLayout_2.addWidget(self.stack_A_lbl)
        self.stack_A = QtWidgets.QLabel(stack_app_window)
        self.stack_A.setObjectName("stack_A")
        self.horizontalLayout_2.addWidget(self.stack_A)
        spacerItem = QtWidgets.QSpacerItem(40, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.horizontalLayout_2.addItem(spacerItem)
        self.regenerate_A_btn = QtWidgets.QPushButton(stack_app_window)
        self.regenerate_A_btn.setObjectName("regenerate_A_btn")
        self.horizontalLayout_2.addWidget(self.regenerate_A_btn)
        self.gridLayout.addLayout(self.horizontalLayout_2, 0, 0, 1, 1)
        self.horizontalLayout_3 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_3.setObjectName("horizontalLayout_3")
        self.stack_B_lbl = QtWidgets.QLabel(stack_app_window)
        self.stack_B_lbl.setObjectName("stack_B_lbl")
        self.horizontalLayout_3.addWidget(self.stack_B_lbl)
        self.stack_B = QtWidgets.QLabel(stack_app_window)
        self.stack_B.setObjectName("stack_B")
        self.horizontalLayout_3.addWidget(self.stack_B)
        spacerItem1 = QtWidgets.QSpacerItem(40, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.horizontalLayout_3.addItem(spacerItem1)
        self.regenerate_B_btn = QtWidgets.QPushButton(stack_app_window)
        self.regenerate_B_btn.setObjectName("regenerate_B_btn")
        self.horizontalLayout_3.addWidget(self.regenerate_B_btn)
        self.gridLayout.addLayout(self.horizontalLayout_3, 1, 0, 1, 1)
        self.result = QtWidgets.QTextEdit(stack_app_window)
        self.result.setReadOnly(True)
        self.result.setObjectName("result")
        self.gridLayout.addWidget(self.result, 2, 0, 1, 1)
        self.horizontalLayout = QtWidgets.QHBoxLayout()
        self.horizontalLayout.setObjectName("horizontalLayout")
        spacerItem2 = QtWidgets.QSpacerItem(488, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.horizontalLayout.addItem(spacerItem2)
        self.compute_btn = QtWidgets.QPushButton(stack_app_window)
        self.compute_btn.setObjectName("compute_btn")
        self.horizontalLayout.addWidget(self.compute_btn)
        self.gridLayout.addLayout(self.horizontalLayout, 3, 0, 1, 1)

        self.retranslateUi(stack_app_window)
        QtCore.QMetaObject.connectSlotsByName(stack_app_window)

    def retranslateUi(self, stack_app_window):
        _translate = QtCore.QCoreApplication.translate
        stack_app_window.setWindowTitle(_translate("stack_app_window", "Exemplul 1 cu PyQt5"))
        self.stack_A_lbl.setText(_translate("stack_app_window", "Multimea A: "))
        self.stack_A.setText(_translate("stack_app_window", "[1, 2, 3]"))
        self.regenerate_A_btn.setText(_translate("stack_app_window", "Generare multimea A"))
        self.stack_B_lbl.setText(_translate("stack_app_window", "Multimea B: "))
        self.stack_B.setText(_translate("stack_app_window", "[4, 5, 6]"))
        self.regenerate_B_btn.setText(_translate("stack_app_window", "Generare multimea B"))
        self.compute_btn.setText(_translate("stack_app_window", "Calculare expresie"))
