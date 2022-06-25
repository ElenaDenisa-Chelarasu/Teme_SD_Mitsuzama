package com.sd.laborator.presentation.config

import com.sd.laborator.business.abstractClasses.AbstractAutoUpdateWorker
import com.sd.laborator.business.interfaces.IFastSearchDAO
import com.sd.laborator.business.interfaces.IFastSearchDAOAdapter
import com.sd.laborator.business.services.AutoUpdateWorker
import com.sd.laborator.business.services.AutoUpdatingFastSearchDAOWithWorkerThread
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class ComponentFactory {
    @Autowired
    private lateinit var _fastSearchDAO: IFastSearchDAO

    @Bean
    @Primary
    open fun createAutoUpdateWorker(): AbstractAutoUpdateWorker {
        return AutoUpdateWorker(_fastSearchDAO)
    }

    @Bean
    @Primary
    open fun createFastSearchDAOJsonAdapter(): IFastSearchDAOAdapter {
        return AutoUpdatingFastSearchDAOWithWorkerThread(_fastSearchDAO)
    }
}