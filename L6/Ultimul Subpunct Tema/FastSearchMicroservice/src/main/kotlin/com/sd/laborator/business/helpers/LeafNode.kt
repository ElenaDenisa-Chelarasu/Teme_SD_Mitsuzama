package com.sd.laborator.business.helpers

import com.sd.laborator.business.interfaces.IMerkleTreeNode
import com.sd.laborator.business.models.CacheItem

class LeafNode:IMerkleTreeNode {
    private val queryResultPairs = mutableListOf<CacheItem>()
    override fun insert(hash: String, cacheItem: CacheItem) {
        //println("Leaf insert...")
        queryResultPairs.add(cacheItem)
    }

    override fun get(hash: String): List<CacheItem> {
        //println("Leaf get: $queryResultPairs")
        return queryResultPairs
    }
}