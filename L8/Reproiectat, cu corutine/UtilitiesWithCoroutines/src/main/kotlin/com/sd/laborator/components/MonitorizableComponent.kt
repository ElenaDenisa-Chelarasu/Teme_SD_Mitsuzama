package com.sd.laborator.components

import com.sd.laborator.helpers.ComponentFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.system.exitProcess

class MonitorizableComponent(private val id: Int) {
    private val heartBeatSocket: Socket

    companion object {
        const val HEART_BEAT_PORT = 1700
    }

    init {
        try {
            heartBeatSocket = Socket(
                ComponentFactory.MESSAGE_MANAGER_HOST,
                HEART_BEAT_PORT
            )
            println("(Monitorizable) M-am conectat la HeartBeatMicroservice!")
        } catch (e: Exception) {
            println("(Monitorizable) Nu m-am putut connecta la HeartBeatMicroservice!")
            exitProcess(1)
        }
    }

    suspend fun run() = coroutineScope {
        val bufferedReader = BufferedReader(InputStreamReader(withContext(Dispatchers.IO) {
            heartBeatSocket.inputStream
        }))
        // Trimitere ID...
        withContext(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                heartBeatSocket.getOutputStream()
            }.write("$id\n".toByteArray())
        }
        var message: String
        while (true) {
            // Receptionare mesaj dummy
            withContext(Dispatchers.IO) {
                message = bufferedReader.readLine()
                //println("(Monitorizable) Am fost verificat de HeartBeatMicroservice: \"$message\"...")
            }
            // Trimitere raspuns
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    heartBeatSocket.getOutputStream()
                }.write("OK!\n".toByteArray())
            }
        }
    }
}