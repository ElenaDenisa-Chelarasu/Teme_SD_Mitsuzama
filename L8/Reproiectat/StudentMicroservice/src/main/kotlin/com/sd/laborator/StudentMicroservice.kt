package com.sd.laborator


import com.sd.laborator.abstractClasses.AbstractPersonMicroservice
import java.io.File


class StudentMicroserviceV3: AbstractPersonMicroservice("StudentMicroservice", STUDENT_PORT, STUDENT_ID) {
    companion object Constants {
        val STUDENT_PORT = System.getenv("PORT")?.toInt() ?: 1650
        val STUDENT_ID = System.getenv("ID")?.toInt() ?: 10
    }

    private var questionDatabase: MutableList<Pair<String, String>>

    init {
        val databaseLines: List<String> = File("questions_database.txt").readLines()
        questionDatabase = mutableListOf()
        // "baza de date" cu intrebari si raspunsuri este de forma:
        // <INTREBARE_1>\n
        // <RASPUNS_INTREBARE_1>\n
        // <INTREBARE_2>\n
        // <RASPUNS_INTREBARE_2>\n
        // ...
        for (i in databaseLines.indices step 2) {
            questionDatabase.add(
                Pair(
                    databaseLines[i],
                    databaseLines[i + 1]
                )
            )
        }
    }

    override fun answerGenerator(message: Message): String? {
        return questionDatabase.firstOrNull { it.first==message.content }?.second
    }

    override fun responseProcessor(message: Message) {
        println("Sunt student, nu ma intereseaza raspunsurile!")
    }
}

fun main(args: Array<String>) {
    val studentMicroservice = StudentMicroserviceV3()//StudentMicroservice()
    studentMicroservice.run()
}