package com.sd.laborator.persons.abstractClasses

import com.sd.laborator.helpers.ComponentFactory
import com.sd.laborator.helpers.Message
import java.net.Socket
import kotlin.random.Random

abstract class AbstractSchoolEmployee(name: String, commandablePort: Int, id: Int) :
    AbstractPersonMicroservice(name, commandablePort, id) {
    companion object {
        const val CATALOG_PORT = 1900
    }

    override fun answerGenerator(message: Message): String? {
        return null
        //return "Raspuns de la $name pentru intrebarea \"${message.content}\": ..."
    }

    override fun responseProcessor(message: Message) {
        val nota = Random.nextInt(5, 11)
        grade(message.source, nota)
        println("Am notat raspunsul \"${message.content}\" cu nota ${nota}.")
    }

    private fun grade(id: String, grade: Int) {
        val catalogSocket = Socket(ComponentFactory.MESSAGE_MANAGER_HOST, CATALOG_PORT)
        catalogSocket.getOutputStream().write("GRADE $id $grade\n".toByteArray())
        catalogSocket.close()
    }
}