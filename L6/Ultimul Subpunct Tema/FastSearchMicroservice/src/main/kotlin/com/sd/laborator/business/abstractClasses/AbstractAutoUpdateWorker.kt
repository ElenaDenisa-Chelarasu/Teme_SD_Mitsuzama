package com.sd.laborator.business.abstractClasses

import com.sd.laborator.business.interfaces.IFastSearchDAO

abstract class AbstractAutoUpdateWorker: Runnable {
    protected abstract val _fastSearchDAO: IFastSearchDAO
    protected abstract val _autoUpdateInterval: Int
}