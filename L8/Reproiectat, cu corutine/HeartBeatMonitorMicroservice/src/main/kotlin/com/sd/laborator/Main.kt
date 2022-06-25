package com.sd.laborator

import com.sd.laborator.generalMicroservices.HeartBeatMicroservice
import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {
    val heartBeatMicroservice = HeartBeatMicroservice()
    heartBeatMicroservice.run()
}