package com.sd.laborator.business.interfaces

interface IFastSearchDAOAdapter {
    fun insert(string: String)
    fun get(string: String): String?
    fun reset()
}