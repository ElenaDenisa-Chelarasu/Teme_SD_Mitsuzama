package com.sd.laborator.business.helpers

import com.sd.laborator.business.interfaces.IMerkleTreeNode
import com.sd.laborator.business.models.CacheItem

class InternalNode:IMerkleTreeNode {
    private val hashCharacterToNode = HashMap<Char, IMerkleTreeNode>()
    override fun insert(hash: String, cacheItem: CacheItem) {
        val currentHashChar=hash[0]
        //println("Internal insert: $currentHashChar")
        val newHash=hash.substring(1)
        var nextNode=hashCharacterToNode[currentHashChar]
        //Inca nu s-a ajuns sa se insereze intr-un nod frunza
        if(nextNode==null){
            nextNode = if(newHash.isNotEmpty())
                InternalNode()
            else
                LeafNode()
            hashCharacterToNode.put(currentHashChar, nextNode)
        }
        nextNode.insert(newHash, cacheItem)
    }

    override fun get(hash: String): List<CacheItem>? {
        val currentHashChar=hash[0]
        //println("Internal get: $currentHashChar")
        val newHash=hash.substring(1)
        val nextNode = hashCharacterToNode[currentHashChar]
        if(nextNode==null)
            return null
        else
            return nextNode.get(newHash)
    }
}