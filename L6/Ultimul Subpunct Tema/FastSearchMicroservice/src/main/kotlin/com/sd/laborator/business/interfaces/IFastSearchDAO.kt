package com.sd.laborator.business.interfaces

import com.sd.laborator.business.models.CacheItem


interface IFastSearchDAO {
    fun insert(cacheItem: CacheItem)
    fun get(query: String): CacheItem?
    fun reset()
}