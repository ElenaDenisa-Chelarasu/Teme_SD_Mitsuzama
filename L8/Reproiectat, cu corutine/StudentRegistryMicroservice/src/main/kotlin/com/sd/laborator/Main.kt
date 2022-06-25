package com.sd.laborator

import com.sd.laborator.generalMicroservices.StudentRegistryMicroservice
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val studentRegistryMicroservice = StudentRegistryMicroservice()
    studentRegistryMicroservice.run()
}