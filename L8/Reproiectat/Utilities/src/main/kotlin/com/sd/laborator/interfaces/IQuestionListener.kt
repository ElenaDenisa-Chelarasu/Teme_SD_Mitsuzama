package com.sd.laborator.interfaces

import com.sd.laborator.Message

interface IQuestionListener {
    fun run()
    fun answerGenerator(question: Message): String?
    fun responseProcessor(response: Message)
}