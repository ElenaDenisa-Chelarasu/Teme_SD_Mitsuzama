package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherMicroservice {
    private lateinit var messageManagerSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1600
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 3000
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        teacherMicroserviceServerSocket = ServerSocket(TEACHER_PORT)

        println("TeacherMicroservice se executa pe portul: ${teacherMicroserviceServerSocket.localPort}")
        println("Se asteapta cereri (intrebari)...")

        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = teacherMicroserviceServerSocket.accept()

            // se foloseste un thread separat pentru tratarea fiecarei conexiuni client
            thread {
                println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val receivedQuestion = clientBufferReader.readLine()

                // intrebarea este redirectionata catre microserviciul MessageManager
                println("Trimit catre MessageManager: ${"intrebare ${messageManagerSocket.localPort} $receivedQuestion\n"}")
                messageManagerSocket.getOutputStream()
                    .write(("intrebare ${messageManagerSocket.localPort} $receivedQuestion\n").toByteArray())

                // se asteapta raspuns de la MessageManager
                val messageManagerBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
                try {
                    val receivedResponse = messageManagerBufferReader.readLine()

                    // se trimite raspunsul inapoi clientului apelant
                    println("Am primit raspunsul: \"$receivedResponse\"")
                    clientConnection.getOutputStream().write((receivedResponse + "\n").toByteArray())
                } catch (e: SocketTimeoutException) {
                    println("Nu a venit niciun raspuns in timp util.")
                    clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                } finally {
                    // se inchide conexiunea cu clientul
                    clientConnection.close()
                }
            }
        }
    }
}

fun main() {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}