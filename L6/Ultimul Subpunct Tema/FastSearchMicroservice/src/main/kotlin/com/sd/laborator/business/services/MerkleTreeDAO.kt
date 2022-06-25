package com.sd.laborator.business.services

import com.sd.laborator.business.helpers.InternalNode
import com.sd.laborator.business.interfaces.IFastSearchDAO
import com.sd.laborator.business.models.CacheItem
import org.springframework.stereotype.Service
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

@Service
class MerkleTreeDAO:IFastSearchDAO {
    private var _root = InternalNode()

    private fun get_md5(string: String): ByteArray{
        val md5 = MessageDigest.getInstance("MD5")
        val bytes = string.toByteArray(UTF_8)
        return md5.digest(bytes)
    }

    private fun ByteArray.toHex(): String {
        val stringList = map { "%02x".format(it) }
        return stringList.joinToString("")
    }

    override fun insert(cacheItem: CacheItem) {
        val hash=get_md5(cacheItem.query).toHex()
        _root.insert(hash, cacheItem)
    }

    override fun get(query: String): CacheItem? {
        val hash=get_md5(query).toHex()
        return _root.get(hash)?.firstOrNull { it.query==query }
    }

    override fun reset(){
        _root= InternalNode()
    }
}