package com.sd.laborator.persistence.entities

class CacheEntity(private var id: Int, private var timestamp: Int, private var query: String, private var result: String) {
    var Id: Int
    get()
    {
        return id
    }
    set(value)
    {
        id=value
    }

    var Timestamp: Int
        get()
        {
            return timestamp
        }
        set(value)
        {
            timestamp=value
        }

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
}