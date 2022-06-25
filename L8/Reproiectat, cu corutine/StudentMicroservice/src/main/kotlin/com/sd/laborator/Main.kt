package com.sd.laborator

import com.sd.laborator.persons.abstractClasses.AbstractPersonMicroservice
import com.sd.laborator.persons.microservices.RegisteredStudentMicroservice
import com.sd.laborator.persons.microservices.UnregisteredStudentMicroservice
import kotlinx.coroutines.runBlocking

val STUDENT_PORT = System.getenv("PORT")?.toInt() ?: 1650
val STUDENT_ID = System.getenv("ID")?.toInt() ?: 10
val STUDENT_TYPE = System.getenv("TYPE")?.toInt()?.mod(2) ?: 1

fun main(args: Array<String>): Unit = runBlocking {
    val studentClasses = listOf(::UnregisteredStudentMicroservice, ::RegisteredStudentMicroservice)
    val studentMicroservice: AbstractPersonMicroservice = studentClasses[STUDENT_TYPE]()
    studentMicroservice.run()
}