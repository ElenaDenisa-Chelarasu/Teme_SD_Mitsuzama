package com.sd.laborator.abstractClasses

import com.sd.laborator.Message
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

abstract class AbstractCommandable(name: String, private val id: Int, private val port: Int, private val messageManagerSocket: Socket):Thread(name) {
    private lateinit var commandableMicroserviceServerSocket: ServerSocket

    private fun deliverAnswersToCommander(clientConnection: Socket) {
        println("(Commandable) S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

        // se citeste intrebarea dorita
        val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
        val receivedCommand = clientBufferReader.readLine()

        // intrebarea este redirectionata catre microserviciul MessageManager
        println("(Commandable) Trimit catre MessageManager: $receivedCommand")
        val message = Message.fromJson(receivedCommand)
        message.source=id.toString()
        messageManagerSocket.getOutputStream().write((message.toJson()+"\n").toByteArray())

        // se asteapta raspuns de la MessageManager
        val messageManagerBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
        var responseReceived=false
        try {
            // Se primesc toate raspunsurile care se pot primi timp de 3000 ms si se trimit pe rand la GUI
            while(true) {
                val receivedResponse = messageManagerBufferReader.readLine()
                val message = Message.fromJson(receivedResponse)
                // se trimite raspunsul inapoi clientului apelant
                println("(Commandable) Am primit raspunsul: $receivedResponse")
                thread { receiveResponse(message) }
                clientConnection.getOutputStream().write((message.content+"\n").toByteArray())
                responseReceived=true
            }
        } catch (e: SocketTimeoutException) {
            // Daca nu s-a primit nici unul atunci se trimite mesajul ...
            if(!responseReceived) {
                clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                println("(Commandable) Nu a venit niciun raspuns in timp util.")
            }
        } finally {
            // se inchide conexiunea cu clientul
            clientConnection.close()
        }
    }

    override fun run() {
        commandableMicroserviceServerSocket = ServerSocket(port)
        println("(Commandable) $name asteapta comenzi pe portul: ${commandableMicroserviceServerSocket.localPort}, avand ID-ul $id")
        println("(Commandable) Se asteapta cereri (intrebari)...")
        while(true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = commandableMicroserviceServerSocket.accept()
            thread { deliverAnswersToCommander(clientConnection) }
        }
    }

    abstract fun receiveResponse(response: Message)
}