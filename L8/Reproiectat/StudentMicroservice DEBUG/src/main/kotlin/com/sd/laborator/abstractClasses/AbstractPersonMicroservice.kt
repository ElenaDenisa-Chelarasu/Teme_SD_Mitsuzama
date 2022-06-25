package com.sd.laborator.abstractClasses

import com.sd.laborator.ComponentFactory
import com.sd.laborator.Message

abstract class AbstractPersonMicroservice(protected val name: String, protected val commandablePort: Int, protected val id: Int) {
    protected val commandable: AbstractCommandable
    protected val questionListener: AbstractQuestionAndResponseListener

    init {
        val factory = ComponentFactory(name, id)
        commandable = factory.createCommandable(commandablePort, ::responseProcessor)
        questionListener = factory.createQuestionListener(::answerGenerator, ::responseProcessor)
    }

    fun run()
    {
        questionListener.start()
        commandable.start()
    }

    protected abstract fun answerGenerator(message: Message): String?
    protected abstract fun responseProcessor(message: Message)
}