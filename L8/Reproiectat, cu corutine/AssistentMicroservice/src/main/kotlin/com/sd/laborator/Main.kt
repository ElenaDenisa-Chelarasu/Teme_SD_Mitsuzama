package com.sd.laborator

import com.sd.laborator.persons.microservices.AssistentMicroservice
import kotlinx.coroutines.runBlocking


fun main(args: Array<String>): Unit = runBlocking {
    val teacherMicroservice = AssistentMicroservice()
    teacherMicroservice.run()
}