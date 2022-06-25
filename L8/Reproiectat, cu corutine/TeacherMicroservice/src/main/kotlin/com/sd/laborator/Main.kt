package com.sd.laborator

import com.sd.laborator.persons.microservices.TeacherMicroservice
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}