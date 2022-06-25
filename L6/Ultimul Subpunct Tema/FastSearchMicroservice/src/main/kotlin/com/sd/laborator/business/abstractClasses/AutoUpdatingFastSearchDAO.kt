package com.sd.laborator.business.abstractClasses

import com.sd.laborator.business.interfaces.IFastSearchDAO
import com.sd.laborator.business.services.FastSearchDAOJsonAdapter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class AutoUpdatingFastSearchDAO(fastSearchDAO: IFastSearchDAO):FastSearchDAOJsonAdapter(fastSearchDAO) {
    protected val _lock = ReentrantLock()

    override fun get(string: String): String? {
        _lock.withLock {
            return super.get(string)
        }
    }

    override fun insert(string: String) {
        _lock.withLock {
            super.insert(string)
        }
    }

    override fun reset() {
        _lock.withLock {
            super.reset()
        }
    }
}