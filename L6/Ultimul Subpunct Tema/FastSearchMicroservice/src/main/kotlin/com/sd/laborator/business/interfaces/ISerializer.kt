package com.sd.laborator.business.interfaces

interface ISerializer<T> {
    fun deserialize(string: String): T?
    fun serialize(item: T): String
}