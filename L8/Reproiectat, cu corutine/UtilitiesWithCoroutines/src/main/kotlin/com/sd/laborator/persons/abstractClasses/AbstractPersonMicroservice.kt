package com.sd.laborator.persons.abstractClasses

import com.sd.laborator.components.abstractClasses.AbstractCommandableComponent
import com.sd.laborator.components.abstractClasses.AbstractQuestionAndResponseListener
import com.sd.laborator.helpers.ComponentFactory
import com.sd.laborator.helpers.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

abstract class AbstractPersonMicroservice(
    protected val name: String,
    protected val commandablePort: Int,
    protected val id: Int
) {
    protected lateinit var commandable: AbstractCommandableComponent
    protected lateinit var questionListener: AbstractQuestionAndResponseListener

    open suspend fun run(): Unit = coroutineScope {
        val factory = ComponentFactory(name, id)
        commandable = factory.createCommandable(commandablePort, ::responseProcessor)
        questionListener = factory.createQuestionListener(::answerGenerator, ::responseProcessor)
        launch { questionListener.run() }
        launch { commandable.run() }
    }

    protected abstract fun answerGenerator(message: Message): String?
    protected abstract fun responseProcessor(message: Message)
}