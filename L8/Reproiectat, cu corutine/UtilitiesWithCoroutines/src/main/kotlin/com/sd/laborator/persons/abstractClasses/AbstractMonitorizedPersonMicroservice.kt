package com.sd.laborator.persons.abstractClasses

import com.sd.laborator.components.MonitorizableComponent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class AbstractMonitorizedPersonMicroservice(name: String, commandablePort: Int, id: Int) :
    AbstractPersonMicroservice(name, commandablePort, id) {
    private val monitorizableComponent = MonitorizableComponent(id)

    override suspend fun run(): Unit = coroutineScope {
        launch { monitorizableComponent.run() }
        launch {
            delay(3000)
            super.run()
        }
    }
}