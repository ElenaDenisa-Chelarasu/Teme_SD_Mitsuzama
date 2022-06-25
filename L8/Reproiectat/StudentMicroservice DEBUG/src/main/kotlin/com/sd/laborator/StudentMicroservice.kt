package com.sd.laborator


import com.sd.laborator.abstractClasses.AbstractPersonMicroservice
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class StudentMicroservice {
    // intrebarile si raspunsurile sunt mentinute intr-o lista de perechi de forma:
    // [<INTREBARE 1, RASPUNS 1>, <INTREBARE 2, RASPUNS 2>, ... ]
    private var questionDatabase: MutableList<Pair<String, String>>
    private lateinit var messageManagerSocket: Socket
    private lateinit var studentMicroserviceServerSocket: ServerSocket

    init {
        val databaseLines: List<String> = File("questions_database.txt").readLines()
        questionDatabase = mutableListOf()
        /*
        "baza de date" cu intrebari si raspunsuri este de forma:
        <INTREBARE_1>\n
        <RASPUNS_INTREBARE_1>\n
        <INTREBARE_2>\n<RASPUNS_INTREBARE_2>\n
        ...
        */
        for (i in databaseLines.indices step 2) {
            questionDatabase.add(
                Pair(
                    databaseLines[i],
                    databaseLines[i + 1]
                )
            )
        }
    }

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        val STUDENT_PORT = System.getenv("PORT")?.toInt() ?: 1650
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun respondToQuestion(question: String): String? {
        questionDatabase.forEach {
            // daca se gaseste raspunsul la intrebare, acesta este returnat apelantului
            if (it.first == question) {
                return it.second
            }
        }
        return null
    }

    fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        // Thread-ul in care se asteapta inrebari de la MessageManager
        thread {
            println("StudentMicroservice se executa pe portul: ${messageManagerSocket.localPort}")
            println("Se asteapta mesaje...")
            val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
            while (true) {
                // se asteapta intrebari trimise prin intermediarul "MessageManager"
                val response = bufferReader.readLine()
                if (response == null) {
                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    println("Microserviciul MessageService (${messageManagerSocket.port}) a fost oprit.")
                    bufferReader.close()
                    messageManagerSocket.close()
                    break
                }
                // se foloseste un thread separat pentru tratarea intrebarii primite
                thread {
                    val (messageType, messageDestination, messageBody) =
                        response.split(" ", limit = 3)
                    when (messageType) {
                        // tipul mesajului cunoscut de acest microserviciu este de forma:
                        // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                        "intrebare" -> {
                            println("Am primit o intrebare de la $messageDestination: \"${messageBody}\"")
                            var responseToQuestion = respondToQuestion(messageBody)
                            responseToQuestion?.let {
                                responseToQuestion = "raspuns $messageDestination $it"
                                println("Trimit raspunsul: \"${response}\"")
                                messageManagerSocket.getOutputStream().write((responseToQuestion + "\n").toByteArray())
                            }
                        }
                    }
                }
            }
        }
        // Thread in care se asteapta conexiuni de la GUI
        thread {
            studentMicroserviceServerSocket = ServerSocket(STUDENT_PORT)
            println("Un student se executa pe portul: ${studentMicroserviceServerSocket.localPort}")
            println("Se asteapta cereri (intrebari)...")
            while (true) {
                // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
                // (in acest caz, din partea aplicatiei client GUI)
                val clientConnection = studentMicroserviceServerSocket.accept()

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
}


class StudentMicroserviceV3: AbstractPersonMicroservice("StudentMicroservice", STUDENT_PORT, STUDENT_ID) {
    companion object Constants {
        val STUDENT_PORT = System.getenv("PORT")?.toInt() ?: 1650
        val STUDENT_ID = System.getenv("ID")?.toInt() ?: 10
    }

    private var questionDatabase: MutableList<Pair<String, String>>

    init {
        val databaseLines: List<String> = File("questions_database.txt").readLines()
        questionDatabase = mutableListOf()
        // "baza de date" cu intrebari si raspunsuri este de forma:
        // <INTREBARE_1>\n
        // <RASPUNS_INTREBARE_1>\n
        // <INTREBARE_2>\n
        // <RASPUNS_INTREBARE_2>\n
        // ...
        for (i in databaseLines.indices step 2) {
            questionDatabase.add(
                Pair(
                    databaseLines[i],
                    databaseLines[i + 1]
                )
            )
        }
    }

    override fun answerGenerator(message: Message): String? {
        return questionDatabase.firstOrNull { it.first==message.content }?.second
    }

    override fun responseProcessor(message: Message) {
        println("Sunt student, nu ma intereseaza raspunsurile!")
    }
}

fun main(args: Array<String>) {
    val studentMicroservice = StudentMicroserviceV3()//StudentMicroservice()
    studentMicroservice.run()
}