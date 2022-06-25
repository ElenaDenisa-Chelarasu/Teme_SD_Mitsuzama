package com.sd.laborator

import com.sd.laborator.generalMicroservices.MessageManagerMicroservice
import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}