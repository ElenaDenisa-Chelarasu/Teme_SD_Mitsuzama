package com.sd.laborator

import com.sd.laborator.abstractClasses.AbstractCommandable
import com.sd.laborator.abstractClasses.AbstractQuestionAndResponseListener
import java.net.Socket
import kotlin.system.exitProcess

class ComponentFactory(private val name: String, private val id: Int) {
    private val messageManagerCommandsSocket: Socket
    private val messageManagerGeneralSocket: Socket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_COMMANDS_PORT = 1500
        const val MESSAGE_MANAGER_GENERAL_PORT = 1501
    }

    init {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        try {
            messageManagerCommandsSocket = Socket(
                MESSAGE_MANAGER_HOST,
                MESSAGE_MANAGER_COMMANDS_PORT
            )

            messageManagerGeneralSocket = Socket(
                MESSAGE_MANAGER_HOST,
                MESSAGE_MANAGER_GENERAL_PORT
            )
            messageManagerGeneralSocket.getOutputStream().write("$id\n".toByteArray())
            messageManagerCommandsSocket.getOutputStream().write("$id\n".toByteArray())
            messageManagerCommandsSocket.soTimeout=3000
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    fun createQuestionListener(answerGenerator: (Message)->String?, responseProcessor: (Message)->Unit): AbstractQuestionAndResponseListener {
        return object: AbstractQuestionAndResponseListener(name, id, messageManagerGeneralSocket) {
            override fun respondToQuestion(question: Message)=answerGenerator(question)
            override fun receiveResponse(response: Message)=responseProcessor(response)
        }
    }

    fun createCommandable(port: Int, responseProcessor: (Message)->Unit): AbstractCommandable{
        return object: AbstractCommandable(name, id, port, messageManagerCommandsSocket) {
            override fun receiveResponse(response: Message)=responseProcessor(response)
        }
    }
}