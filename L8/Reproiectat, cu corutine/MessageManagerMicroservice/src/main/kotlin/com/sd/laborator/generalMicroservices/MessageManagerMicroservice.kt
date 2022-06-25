package com.sd.laborator.generalMicroservices

import com.sd.laborator.helpers.ComponentFactory
import com.sd.laborator.helpers.Message
import com.sd.laborator.helpers.SocketPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class MessageManagerMicroservice {
    private val subscribers = HashMap<Int, SocketPair>()

    private val messageManagerCommandsSocket: ServerSocket
    private val messageManagerGeneralSocket: ServerSocket

    companion object Constants {
        const val MESSAGE_MANAGER_COMMANDS_PORT = 1500
        const val MESSAGE_MANAGER_GENERAL_PORT = 1501
        const val ASSISTENT_PORT = 2100
        const val ASSISTENT_ID = 21
        const val TEACHER_ID = 20
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
        val bytes = (json + "\n").toByteArray()
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

    private suspend fun receiveCommandedQuestion() = coroutineScope {
        while (true) {
            // Accept conexiunea clientului comandat
            val commandedClientConnection = withContext(Dispatchers.IO) {
                messageManagerCommandsSocket.accept()
            }
            // Tratez receptionarea intrebarii lui si trimiterea raspunsului intr-un alt thread
            launch {
                val bufferReader = BufferedReader(InputStreamReader(commandedClientConnection.inputStream))
                // Primul mesaj e mereu ID-ul, un int...
                var receivedMessage = withContext(Dispatchers.IO) {
                    bufferReader.readLine()
                }
                if (messageIsNull(receivedMessage, bufferReader, commandedClientConnection))
                    return@launch
                val id = receivedMessage.toInt()
                if (!idIsValid(id))
                    return@launch
                println("CommandableSubscriber conectat: $id")
                synchronized(subscribers) {
                    if (id in subscribers)
                        subscribers[id]!!.commandable = commandedClientConnection
                    else
                        subscribers[id] = SocketPair(commandable = commandedClientConnection)
                }
                while (true) {
                    // Se citeste raspunsul de pe socketul TCP
                    receivedMessage = withContext(Dispatchers.IO) {
                        bufferReader.readLine()
                    }
                    // Daca mesajul e nul se elibereaza conexiunea
                    if (messageIsNull(receivedMessage, bufferReader, commandedClientConnection)) {
                        synchronized(subscribers) {
                            if (subscribers[id]!!.general == null)
                                subscribers.remove(id)
                            else
                                subscribers[id]!!.commandable = null
                        }
                        println("S-a deconectat CommandableSubscriber cu ID $id")
                        return@launch
                    }
                    println("Primit mesaj: $receivedMessage")
                    askQuestion(receivedMessage)
                }
            }
        }
    }

    private fun routeResponse(json: String) {
        val message = Message.fromJson(json)
        val bytes = (json + "\n").toByteArray()
        synchronized(subscribers) {
            subscribers[message.destination.toInt()]?.commandable?.getOutputStream()?.write(bytes)
            if (message.responseType == "Public")
                subscribers.filterNot { it.key == message.destination.toInt() || it.key == message.source.toInt() }
                    .forEach { it.value.general?.getOutputStream()?.write(bytes) }
        }
    }

    private suspend fun receiveResponses() = coroutineScope {
        while (true) {
            // Accept conexiunea clientului care raspunde la intrebari si asculta raspunsuri
            val generalClientConnection = withContext(Dispatchers.IO) {
                messageManagerGeneralSocket.accept()
            }
            // Tratez primirea de raspunsuri si trimiterea de raspunsuri intr-un thread separat
            //thread {
            launch {
                val bufferReader = BufferedReader(InputStreamReader(generalClientConnection.inputStream))
                // Primul mesaj e mereu ID-ul, un int...
                var receivedMessage = withContext(Dispatchers.IO) {
                    bufferReader.readLine()
                }

                if (messageIsNull(receivedMessage, bufferReader, generalClientConnection))
                    return@launch
                val id = receivedMessage.toInt()
                if (!idIsValid(id))
                    return@launch
                println("GeneralSubscriber conectat: $id")
                synchronized(subscribers) {
                    if (id in subscribers)
                        subscribers[id]!!.general = generalClientConnection
                    else
                        subscribers[id] = SocketPair(general = generalClientConnection)
                }
                while (true) {
                    // Se citeste raspunsul de pe socketul TCP
                    receivedMessage = withContext(Dispatchers.IO) {
                        bufferReader.readLine()
                    }
                    // Daca mesajul e nul se elibereaza conexiunea
                    if (messageIsNull(receivedMessage, bufferReader, generalClientConnection)) {
                        synchronized(subscribers) {
                            if (subscribers[id]!!.commandable == null)
                                subscribers.remove(id)
                            else
                                subscribers[id]!!.general = null
                        }
                        println("S-a deconectat GeneralSubscriber cu ID $id")

                        return@launch
                    }
                    println("Primit mesaj: $receivedMessage")
                    routeResponse(receivedMessage)
                }
            }
        }
    }

    private fun idIsValid(id: Int): Boolean {
        if (id == TEACHER_ID || id == ASSISTENT_ID)
            return true
        val assistentValidateConnexionsSocket = Socket(ComponentFactory.MESSAGE_MANAGER_HOST, ASSISTENT_PORT)
        assistentValidateConnexionsSocket.getOutputStream().write("$id\n".toByteArray())
        val assistentBufferedReader = BufferedReader(InputStreamReader(assistentValidateConnexionsSocket.inputStream))
        val isValid = assistentBufferedReader.readLine()
        println("$id is $isValid")
        return isValid == "VALID"
    }

    suspend fun run() = coroutineScope {
        println("MessageManagerMicroservice a pornit!")
        launch { receiveCommandedQuestion() }
        launch { receiveResponses() }
    }
}