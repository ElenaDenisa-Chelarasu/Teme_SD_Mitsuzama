package com.sd.laborator.abstractClasses

import com.sd.laborator.ComponentFactory
import java.net.Socket
import kotlin.system.exitProcess

class MonitorizableComponent {
    private val heartBeatSocket: Socket

    companion object {
        val HEART_BEAT_PORT = 1700
    }

    init {
        try {
            heartBeatSocket = Socket(
                ComponentFactory.MESSAGE_MANAGER_HOST,
                HEART_BEAT_PORT
            )
            println("(Monitorized) M-am conectat la HeartBeatMicroservice!")
        }catch(e: Exception){
            println("(Monitorized) Nu m-am putut connecta la HeartBeatMicroservice!")
            exitProcess(1)
        }
    }
}