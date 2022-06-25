package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ISerializer
import com.sd.laborator.business.models.CacheItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service

@Service
class CacheItemToAndFromJsonSerializer:ISerializer<CacheItem> {
    override fun deserialize(string: String): CacheItem? {
        val map= Json.decodeFromString<Map<String, String>>(string)
        return try {
            CacheItem(map["query"]!!, map["result"]!!, map["timestamp"]!!.toInt())
        }catch(e: NullPointerException){
            null
        }
    }

    override fun serialize(item: CacheItem): String {
        val query =escapeString(item.query)
        val result=escapeString(item.result)
        val timestamp=item.timestamp.toString()
        return "{\"query\":\"$query\",\"result\":\"$result\",\"timestamp\":\"$timestamp\"}"
    }

    companion object{
        fun escapeString(string: String): String {
            return string
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        }
    }
}