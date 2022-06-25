package com.sd.laborator.helpers

import java.io.File

class StudentFunctionalityDelegate {
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

    fun answerGenerator(message: Message): String? {
        return questionDatabase.firstOrNull { it.first == message.content }?.second
    }

    fun responseProcessor(message: Message) {
        println("Sunt student, nu ma intereseaza raspunsurile!")
    }
}