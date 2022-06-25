package com.sd.laborator.business.models

import com.sd.laborator.persistence.entities.CacheEntity

class CacheModel(private var query: String, private var result: String) {
    var Query: String
    get()
    {
        return query
    }
    set(value)
    {
        query=value
    }

    var Result: String
        get()
        {
            return result
        }
        set(value)
        {
            result=value
        }

    fun ToEntity(): CacheEntity{
        return CacheEntity(0, (System.currentTimeMillis()/1000).toInt(), query, result)
    }
}