package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ICachingService
import com.sd.laborator.business.models.CacheModel
import com.sd.laborator.persistence.interfaces.ICachingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CachingService:ICachingService {
    @Autowired
    private lateinit var cachingRepository: ICachingRepository

    @Value("\${expirationSeconds}")
    private val expirationSeconds = 0

    override fun exists(query: String): String? {
        val item = cachingRepository.getByQuery(query) ?: return null
        val currentTime = (System.currentTimeMillis()/1000).toInt()
        return if(currentTime - item.Timestamp <= expirationSeconds)
            item.Result
        else
            null
    }

    override fun addToCache(query: String, result: String) {
        val cacheModel = CacheModel(query, result)
        cachingRepository.add(cacheModel.ToEntity())
    }
}