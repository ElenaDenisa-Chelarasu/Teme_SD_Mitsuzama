package com.sd.laborator.generalMicroservices

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.system.exitProcess


class CatalogMicroservice {
    private val catalogSocket: ServerSocket
    private val catalog = HashMap<String, MutableList<Int>>()

    companion object {
        const val CATALOG_PORT = 1900
    }

    init {
        try {
            catalogSocket = ServerSocket(CATALOG_PORT)

            println("CatalogMicroservice a pornit!")
        } catch (e: Exception) {
            println("CatalogMicroservice nu a putut porni!")
            exitProcess(1)
        }
    }

    private suspend fun serveClient(clientConnection: Socket): Unit = coroutineScope {
        val bufferedReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
        try {
            while (true) {
                val message = withContext(Dispatchers.IO) {
                    bufferedReader.readLine()
                }.split(" ")
                processMessage(message)
            }
        } finally {
            withContext(Dispatchers.IO) {
                bufferedReader.close()
            }
            withContext(Dispatchers.IO) {
                clientConnection.close()
            }
        }
    }

    private fun processMessage(message: List<String>) {
        when (message[0]) {
            "ADD" -> {
                if (message.size != 2) {
                    println("Mesaj in format invalid: ${message.joinToString(" ")}")
                    return
                }
                synchronized(catalog) {
                    catalog[message[1]] = mutableListOf()
                    println("S-a adaugat studentul cu ID-ul ${message[1]} in baza de date!")
                }
            }
            "REMOVE" -> {
                if (message.size != 2) {
                    println("Mesaj in format invalid: ${message.joinToString(" ")}")
                    return
                }
                synchronized(catalog) {
                    val note = catalog[message[1]]
                    if (note == null)
                        println("S-a incercat finalizarea situatiei unui student neinregistrat (cu ID-ul ${message[1]}")
                    else if (note.size == 0)
                        println("Situatie finala a studentului cu ID-ul ${message[1]}: ABSENT (0 note luate)")
                    else
                        println("Situatie finala a studentului cu ID-ul ${message[1]}: Note: $note, Media: ${note.average()}")
                }
            }
            "GRADE" -> {
                if (message.size != 3) {
                    println("Mesaj in format invalid: ${message.joinToString(" ")}")
                    return
                }
                synchronized(catalog) {
                    val note = catalog[message[1]]
                    if (note == null)
                        println("S-a incercat adaugarea unei note unui student neinregistrat (cu ID-ul ${message[1]}")
                    else if (!Regex("^[1-9]|(10)$").matches(message[2]))
                        println("S-a incercat adaugarea unei note invalide: ${message[2]}")
                    else {
                        note.add(message[2].toInt())
                        println("S-a adaugat nota ${message[2]} studentului cu ID-ul ${message[1]}")
                    }
                }
            }
        }
    }

    suspend fun run(): Unit = coroutineScope {
        try {
            while (true) {
                val clientConnection = withContext(Dispatchers.IO) {
                    catalogSocket.accept()
                }
                launch { serveClient(clientConnection) }
            }
        } finally {
            withContext(Dispatchers.IO) {
                catalogSocket.close()
            }
        }
    }
}
