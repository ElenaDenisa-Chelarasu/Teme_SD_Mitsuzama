package com.sd.laborator.business.interfaces

interface ICachingService {
    fun exists(query: String): String?
    fun addToCache(query: String, result: String)
}