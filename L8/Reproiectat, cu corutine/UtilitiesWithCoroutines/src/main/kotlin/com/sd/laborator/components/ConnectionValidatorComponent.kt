package com.sd.laborator.components

import com.sd.laborator.helpers.ComponentFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.system.exitProcess

class ConnectionValidatorComponent(port: Int) {
    private val connectionValidatorSocket: ServerSocket

    companion object {
        const val REGISTRY_PORT = 1800
    }

    init {
        try {
            connectionValidatorSocket = ServerSocket(port)
            println("(ConnectionValidator) S-a deschis ServerSocket-ul pentru validarea conexiunilor")
        } catch (e: Exception) {
            println("(ConnectionValidator) Nu s-a putut deschide ServerSocket-ul pentru validarea conexiunilor!")
            exitProcess(1)
        }
    }

    suspend fun run(): Unit = coroutineScope {
        validateConnections()
    }

    private suspend fun validateConnections(): Unit = coroutineScope {
        try {
            while (true) {
                val clientConnexion = withContext(Dispatchers.IO) {
                    connectionValidatorSocket.accept()
                }
                launch { serveClient(clientConnexion) }
            }
        } finally {
            withContext(Dispatchers.IO) {
                connectionValidatorSocket.close()
            }
        }
    }

    private suspend fun serveClient(clientConnexion: Socket): Unit = coroutineScope {
        // Obtin buffered reader-ul de la client
        val bufferedReader = BufferedReader(InputStreamReader(clientConnexion.inputStream))
        try {
            // Mereu se va verifica doar cate 1 ID
            val id = withContext(Dispatchers.IO) {
                bufferedReader.readLine()
            }
            println("De verificat ID: $id")
            // Rezultatul de la StudentRegistry
            var isValid: String
            // Socketul de la StudentRegistry
            val registrySocket: Socket
            try {
                // Conectare la StudentRegistry
                registrySocket = withContext(Dispatchers.IO) {
                    Socket(ComponentFactory.MESSAGE_MANAGER_HOST, REGISTRY_PORT)
                }
                // Obtinem buffered reader de la StudentRegistry
                val registryBufferedReader = BufferedReader(InputStreamReader(registrySocket.inputStream))
                // Trimitem cerere de verificare a id-ului
                withContext(Dispatchers.IO) {
                    withContext(Dispatchers.IO) {
                        registrySocket.getOutputStream()
                    }.write("CHECK $id\n".toByteArray())
                }
                // Citim rezultatul verificarii
                isValid = withContext(Dispatchers.IO) {
                    registryBufferedReader.readLine()
                }
            } catch (e: Exception) {
                // Daca nu se reuseste conexiunea la StudentRegistry, atunci toate id-urile vor fi considerate valide
                println("Nu s-a reusit conectarea la StudentRegistry => ID-ul $id va fi considerat valid!")
                isValid = "VALID"
            }
            // Trimit clientului rezultatul verificarii
            println("Trimit rezultatul clientului: $isValid")
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    clientConnexion.getOutputStream()
                }.write("$isValid\n".toByteArray())
            }
        } finally {
            // Dupa ce se inchide conexiunea in celalalt capat inchidem buffered reader si capatul nostru de conexiune
            withContext(Dispatchers.IO) {
                bufferedReader.close()
            }
            withContext(Dispatchers.IO) {
                clientConnexion.close()
            }
        }
    }
}