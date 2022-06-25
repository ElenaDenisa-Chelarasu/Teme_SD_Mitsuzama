package com.sd.laborator.persons.microservices

import com.sd.laborator.STUDENT_ID
import com.sd.laborator.STUDENT_PORT
import com.sd.laborator.helpers.StudentFunctionalityDelegate
import com.sd.laborator.helpers.Message
import com.sd.laborator.persons.abstractClasses.AbstractMonitorizedPersonMicroservice

class RegisteredStudentMicroservice :
    AbstractMonitorizedPersonMicroservice("RegisteredStudentMicroservice", STUDENT_PORT, STUDENT_ID) {
    private val functionalityDelegate = StudentFunctionalityDelegate()

    override fun answerGenerator(message: Message): String? = functionalityDelegate.answerGenerator(message)
    override fun responseProcessor(message: Message) = functionalityDelegate.responseProcessor(message)
}