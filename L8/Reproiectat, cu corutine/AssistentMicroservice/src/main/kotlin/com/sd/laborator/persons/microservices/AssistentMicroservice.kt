package com.sd.laborator.persons.microservices

import com.sd.laborator.components.ConnectionValidatorComponent
import com.sd.laborator.persons.abstractClasses.AbstractSchoolEmployee
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AssistentMicroservice
    : AbstractSchoolEmployee("AssistentMicroservicce", ASSISTENT_PORT, ASSISTENT_ID) {
    private val connectionValidatorComponent = ConnectionValidatorComponent(CONNECTION_VALIDATOR_PORT)

    companion object Constants {
        const val ASSISTENT_ID = 21
        const val ASSISTENT_PORT = 2000
        const val CONNECTION_VALIDATOR_PORT = 2100
    }

    override suspend fun run(): Unit = coroutineScope {
        launch { super.run() }
        launch { connectionValidatorComponent.run() }
    }
}