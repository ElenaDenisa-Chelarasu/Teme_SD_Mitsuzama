package com.sd.laborator.business.services

import com.sd.laborator.business.abstractClasses.AbstractAutoUpdateWorker
import com.sd.laborator.business.abstractClasses.AutoUpdatingFastSearchDAO
import com.sd.laborator.business.helpers.CacheItemRowMapper
import com.sd.laborator.business.interfaces.IFastSearchDAO
import com.sd.laborator.business.models.CacheItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

@Service
class AutoUpdatingFastSearchDAOWithWorkerThread(fastSearchDAO: IFastSearchDAO):AutoUpdatingFastSearchDAO(fastSearchDAO) {

    @Autowired
    private lateinit var _autoUpdateWorkerThread : AbstractAutoUpdateWorker

    private var workerStarted = false

    override fun get(string: String): String? {
        if(!workerStarted) {
            _lock.withLock {
                if(!workerStarted){
                    workerStarted=true
                    Thread(_autoUpdateWorkerThread).start()
                }
            }
        }
        return super.get(string)
    }
}