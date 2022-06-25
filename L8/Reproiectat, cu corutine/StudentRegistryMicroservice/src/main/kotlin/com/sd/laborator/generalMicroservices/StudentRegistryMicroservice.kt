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


class StudentRegistryMicroservice {
    private val registrySocket: ServerSocket
    private val catalogSocket: Socket
    private val idSet = mutableSetOf<String>()

    companion object {
        const val REGISTRY_PORT = 1800
        const val CATALOG_PORT = 1900
        val CATALOG_HOST = System.getenv("CATALOG_HOST") ?: "localhost"
    }

    init {
        try {
            catalogSocket = Socket(CATALOG_HOST, CATALOG_PORT)
            println("StudentRegistryMicroservice s-a conectat la catalog!")
        } catch (e: Exception) {
            println("StudentRegistryMicroservice nu s-a putut conecta la catalog!")
            exitProcess(1)
        }
        try {
            registrySocket = ServerSocket(REGISTRY_PORT)
            println("StudentRegistryMicroservice a pornit!")
        } catch (e: Exception) {
            println("StudentRegistryMicroservice nu a putut porni!")
            exitProcess(1)
        }
    }

    suspend fun run(): Unit = coroutineScope {
        try {
            while (true) {
                val clientConnection = withContext(Dispatchers.IO) {
                    registrySocket.accept()
                }
                launch { serveClient(clientConnection) }
            }
        } finally {
            withContext(Dispatchers.IO) {
                registrySocket.close()
            }
        }
    }

    private suspend fun serveClient(clientConnection: Socket): Unit = coroutineScope {
        val bufferedReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
        try {
            while (true) {
                val (command, id) = withContext(Dispatchers.IO) {
                    bufferedReader.readLine()
                }.split(" ", limit = 2)
                when (command) {
                    "ADD" -> {
                        println("Studentul cu ID-ul $id s-a inregistrat!")
                        synchronized(idSet) {
                            idSet.add(id)
                        }
                        withContext(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                catalogSocket.getOutputStream()
                            }.write("ADD $id\n".toByteArray())
                        }
                    }
                    "REMOVE" -> {
                        println("Studentul cu ID-ul $id nu mai este inregistrat!")
                        synchronized(idSet) {
                            idSet.remove(id)
                        }
                        withContext(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                catalogSocket.getOutputStream()
                            }.write("REMOVE $id\n".toByteArray())
                        }
                    }
                    "CHECK" -> {
                        val isRegistered: String
                        synchronized(idSet) {
                            if (idSet.contains(id))
                                isRegistered = "VALID\n"
                            else
                                isRegistered = "INVALID\n"
                        }
                        println("Rezultatul verificarii inregistrarii studentului cu ID-ul $id: $isRegistered")
                        withContext(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                clientConnection.getOutputStream()
                            }.write(isRegistered.toByteArray())
                        }
                    }
                }
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
}