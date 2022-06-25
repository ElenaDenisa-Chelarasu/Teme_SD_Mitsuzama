package com.sd.laborator.dbManagers.interfaces

interface IButtonsDBManager {
    fun getButtonNamesAndFrequencies(): Map<String, Int>
}