package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IFastSearchDAO
import com.sd.laborator.business.interfaces.IFastSearchDAOAdapter
import com.sd.laborator.business.interfaces.ISerializer
import com.sd.laborator.business.models.CacheItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
open class FastSearchDAOJsonAdapter(private val _adaptee: IFastSearchDAO):IFastSearchDAOAdapter {
    @Value("\${expirationSeconds}")
    private val expirationSeconds: Int = 0

    @Autowired
    private lateinit var _serializer: ISerializer<CacheItem>
    override fun insert(string: String) {
        _serializer.deserialize(string)?.let {
            _adaptee.insert(it)
        }
    }

    override fun get(string: String): String? {
        val result = _adaptee.get(string) ?: return null
        val currentSeconds = (System.currentTimeMillis()/1000L).toInt()
        return if(currentSeconds - result.timestamp <= expirationSeconds)
            _serializer.serialize(result)
        else
            null
    }

    override fun reset(){
        _adaptee.reset()
    }
}