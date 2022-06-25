package com.sd.laborator


import com.sd.laborator.abstractClasses.AbstractPersonMicroservice
import java.io.File
import kotlin.random.Random

class TeacherMicroservice: AbstractPersonMicroservice("TeacherMicroservice", TEACHER_PORT, TEACHER_ID) {
    companion object Constants {
        val TEACHER_PORT = 1600
        val TEACHER_ID = 20
    }

    override fun answerGenerator(message: Message): String? {
        return "Raspuns de la Teacher pentru intrebarea \"${message.content}\": ..."
    }

    override fun responseProcessor(message: Message) {
        val nota = Random.nextInt(5,11)
        println("Am notat raspunsul \"${message.content}\" cu nota ${nota}.")
    }
}

fun main(args: Array<String>) {
    val teacherMicroservice = TeacherMicroservice()//StudentMicroservice()
    teacherMicroservice.run()
}