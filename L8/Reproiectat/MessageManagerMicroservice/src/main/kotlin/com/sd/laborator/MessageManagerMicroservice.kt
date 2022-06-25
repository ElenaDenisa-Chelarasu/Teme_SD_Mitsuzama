package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

data class SocketPair(var commandable: Socket?=null, var general: Socket?=null)

class MessageManagerMicroservice {
    private val subscribers = HashMap<Int, SocketPair>()

    private val messageManagerCommandsSocket: ServerSocket
    private val messageManagerGeneralSocket: ServerSocket

    companion object Constants {
        const val MESSAGE_MANAGER_COMMANDS_PORT = 1500
        const val MESSAGE_MANAGER_GENERAL_PORT = 1501
    }

    init {
        messageManagerCommandsSocket = ServerSocket(MESSAGE_MANAGER_COMMANDS_PORT)
        messageManagerGeneralSocket = ServerSocket(MESSAGE_MANAGER_GENERAL_PORT)
    }

    private fun messageIsNull(message: String?, br: BufferedReader, socket: Socket): Boolean {
        if (message != null)
            return false
        br.close()
        socket.close()
        return true
    }

    private fun askQuestion(json: String) {
        val message = Message.fromJson(json)
        val bytes = (json+"\n").toByteArray()
        synchronized(subscribers) {
            if (message.destinationType == "1to1")
                subscribers[message.destination.toInt()]?.general?.getOutputStream()?.write(bytes)
            else if (message.destinationType == "1toa")
                subscribers.filterNot { it.key == message.source.toInt() }
                    .forEach { it.value.general?.getOutputStream()?.write(bytes) }
            else    // Nu stiu de ce vrea sa pun si ramura else...
                return
        }
    }

    fun receiveCommandedQuestion() {
        while (true) {
            // Accept conexiunea clientului comandat
            val commandedClientConnection = messageManagerCommandsSocket.accept()
            // Tratez receptionarea intrebarii lui si trimiterea raspunsului intr-un alt thread
            thread {
                val bufferReader = BufferedReader(InputStreamReader(commandedClientConnection.inputStream))
                // Primul mesaj e mereu ID-ul, un int...
                var receivedMessage = bufferReader.readLine()
                if (messageIsNull(receivedMessage, bufferReader, commandedClientConnection))
                    return@thread
                val id = receivedMessage.toInt()
                println("CommandableSubscriber conectat: $id")
                synchronized(subscribers) {
                    if (id in subscribers)
                        subscribers[id]!!.commandable = commandedClientConnection
                    else
                        subscribers[id] = SocketPair(commandable = commandedClientConnection)
                }
                while (true) {
                    // Se citeste raspunsul de pe socketul TCP
                    receivedMessage = bufferReader.readLine()
                    // Daca mesajul e nul se elibereaza conexiunea
                    if (messageIsNull(receivedMessage, bufferReader, commandedClientConnection)) {
                        synchronized(subscribers) {
                            if (subscribers[id]!!.general == null)
                                subscribers.remove(id)
                            else
                                subscribers[id]!!.commandable = null
                        }
                        println("S-a deconectat CommandableSubscriber cu ID $id")
                        return@thread
                    }
                    println("Primit mesaj: $receivedMessage")
                    askQuestion(receivedMessage)
                }
            }
        }
    }

    private fun routeResponse(json: String) {
        val message=Message.fromJson(json)
        val bytes = (json+"\n").toByteArray()
        synchronized(subscribers) {
            subscribers[message.destination.toInt()]?.commandable?.getOutputStream()?.write(bytes)
            if(message.responseType=="Public")
                subscribers.filterNot { it.key==message.destination.toInt() || it.key==message.source.toInt() }
                    .forEach { it.value.general?.getOutputStream()?.write(bytes) }
        }
    }

    private fun receiveResponses(){
        while(true) {
            // Accept conexiunea clientului care raspunde la intrebari si asculta raspunsuri
            val generalClientConnection = messageManagerGeneralSocket.accept()
            // Tratez primirea de raspunsuri si trimiterea de raspunsuri intr-un thread separat
            thread {
                val bufferReader = BufferedReader(InputStreamReader(generalClientConnection.inputStream))
                // Primul mesaj e mereu ID-ul, un int...
                var receivedMessage = bufferReader.readLine()
                if (messageIsNull(receivedMessage, bufferReader, generalClientConnection))
                    return@thread
                val id = receivedMessage.toInt()
                println("GeneralSubscriber conectat: $id")
                synchronized(subscribers) {
                    if (id in subscribers)
                        subscribers[id]!!.general = generalClientConnection
                    else
                        subscribers[id] = SocketPair(general = generalClientConnection)
                }
                while (true) {
                    // Se citeste raspunsul de pe socketul TCP
                    receivedMessage = bufferReader.readLine()
                    // Daca mesajul e nul se elibereaza conexiunea
                    if (messageIsNull(receivedMessage, bufferReader, generalClientConnection)) {
                        synchronized(subscribers) {
                            if (subscribers[id]!!.commandable == null)
                                subscribers.remove(id)
                            else
                                subscribers[id]!!.general = null
                        }
                        println("S-a deconectat GeneralSubscriber cu ID $id")

                        return@thread
                    }
                    println("Primit mesaj: $receivedMessage")
                    routeResponse(receivedMessage)
                }
            }
        }
    }

    fun run() {
        thread { receiveResponses() }
        receiveCommandedQuestion()
    }
}

fun main() {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}