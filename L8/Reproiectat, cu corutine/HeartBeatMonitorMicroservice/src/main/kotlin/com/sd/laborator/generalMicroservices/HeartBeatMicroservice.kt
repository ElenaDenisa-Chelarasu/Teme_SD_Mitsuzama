package com.sd.laborator.generalMicroservices

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.system.exitProcess


class HeartBeatMicroservice {
    private val heartBeatSocket: ServerSocket
    private val registrySocket: Socket

    companion object {
        const val HEART_BEAT_PORT = 1700
        val REGISTRY_HOST = System.getenv("REGISTRY_HOST") ?: "localhost"
        const val REGISTRY_PORT = 1800
    }

    init {
        try {
            registrySocket = Socket(REGISTRY_HOST, REGISTRY_PORT)
            println("HeartBeatMicroservice s-a conectat la StudentRegistryMicroservice!")
        } catch (e: Exception) {
            println("HeartBeatMicroservice nu s-a putut conecta la StudentRegistryMicroservice!")
            exitProcess(1)
        }
        try {
            heartBeatSocket = ServerSocket(HEART_BEAT_PORT)
            println("HeartBeatMicroservice pornit!")
        } catch (e: Exception) {
            println("HeartBeatMicroservice nu a putut deschide ServerSocket-ul!")
            exitProcess(1)
        }
    }

    suspend fun run() = coroutineScope {
        try {
            while (true) {
                val clientConnection = withContext(Dispatchers.IO) {
                    heartBeatSocket.accept()
                }
                launch {
                    clientConnection.soTimeout = 3000
                    val bufferedReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                    try {
                        val id = withContext(Dispatchers.IO) {
                            bufferedReader.readLine()
                        }.toInt()
                        // Adaugare student in Registry
                        withContext(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                registrySocket.getOutputStream()
                            }.write("ADD $id\n".toByteArray())
                        }
                        println("A inceput monitorizarea studentului cu ID-ul $id...")
                        while (true) {
                            delay(3000)
                            try {
                                withContext(Dispatchers.IO) {
                                    withContext(Dispatchers.IO) {
                                        clientConnection.getOutputStream()
                                    }.write("OK?\n".toByteArray())
                                }
                                withContext(Dispatchers.IO) {
                                    bufferedReader.readLine()
                                }
                            } catch (e: Exception) {
                                println("Studentul cu ID-ul $id nu mai raspunde...")
                                withContext(Dispatchers.IO) {
                                    withContext(Dispatchers.IO) {
                                        registrySocket.getOutputStream()
                                    }.write("REMOVE $id\n".toByteArray())
                                }
                                return@launch
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
        } finally {
            withContext(Dispatchers.IO) {
                heartBeatSocket.close()
            }
            withContext(Dispatchers.IO) {
                registrySocket.close()
            }
        }
    }
}