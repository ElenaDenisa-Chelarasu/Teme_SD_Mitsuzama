package com.sd.laborator.abstractClasses

import com.sd.laborator.Message
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.concurrent.thread

// Primeste intrebari si raspunde la ele
// Primeste raspunsuri si le trateaza:
    // Studentii spun ca nu sunt interesati de raspunsuri
    // Profesorul noteaza cu o nota random raspunsul
// Nu se vor trimite aici raspunsurile unei intrebari puse de microserviciul care incapsuleaza obiectul
abstract class AbstractQuestionAndResponseListener(name: String, private val id: Int, private val messageManagerSocket: Socket):Thread(name) {
    override fun run() {
        //messageManagerSocket.getOutputStream().write(id.toString().toByteArray())
        println("(QuestionAndResponseListener) $name asteapta intrebari si raspunsuri pe portul: ${messageManagerSocket.localPort}, avand ID-ul $id")
        println("(QuestionAndResponseListener) Se asteapta mesaje...")
        val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
        while (true) {
            // se asteapta intrebari trimise prin intermediarul "MessageManager"
            val response = bufferReader.readLine()
            if (response == null) {
                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                println("(QuestionAndResponseListener) Microserviciul MessageService (${messageManagerSocket.port}) a fost oprit.")
                bufferReader.close()
                messageManagerSocket.close()
                break
            }
            // se foloseste un thread separat pentru tratarea intrebarii primite
            thread {
                // Transformare JSON in Message
                val message = Message.fromJson(response)
                when (message.type) {
                    // tipul mesajului cunoscut de acest microserviciu este de forma:
                    // JSON cu campuri de aceleasi nume cu campurile din clasa Message
                    "intrebare" -> {
                        println("(QuestionAndResponseListener) Am primit o intrebare: $response")
                        var responseToQuestion = respondToQuestion(message)
                        responseToQuestion?.let {
                            message.type="raspuns"
                            message.destination = message.source
                            message.source = id.toString()
                            message.content=it
                            // cand se rapsunde, destinationType este ignorat
                            // responseType ramane cel dorit de cel ce pune intrebarea

                            responseToQuestion = message.toJson()
                            println("(QuestionAndResponseListener) Trimit raspunsul: $responseToQuestion")
                            messageManagerSocket.getOutputStream().write((responseToQuestion + "\n").toByteArray())
                        }
                    }
                    "raspuns" -> {
                        println("(QuestionAndResponseListener) Am primit un raspuns: $response")
                        receiveResponse(message)
                    }
                }
            }
        }
    }

    abstract fun respondToQuestion(question: Message): String?
    abstract fun receiveResponse(response: Message)
}