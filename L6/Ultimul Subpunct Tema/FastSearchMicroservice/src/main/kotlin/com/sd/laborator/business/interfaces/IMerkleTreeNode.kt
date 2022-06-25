package com.sd.laborator.business.interfaces

import com.sd.laborator.business.models.CacheItem


interface IMerkleTreeNode {
    fun insert(hash: String, cacheItem: CacheItem)
    fun get(hash: String): List<CacheItem>?
}