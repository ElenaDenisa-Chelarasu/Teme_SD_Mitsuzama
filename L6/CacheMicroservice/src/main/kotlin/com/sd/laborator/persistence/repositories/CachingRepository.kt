package com.sd.laborator.persistence.repositories

import com.sd.laborator.persistence.entities.CacheEntity
import com.sd.laborator.persistence.interfaces.ICachingRepository
import com.sd.laborator.persistence.mappers.CacheRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
open class CachingRepository:ICachingRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private val _mapper:RowMapper<CacheEntity> = CacheRowMapper()
    private fun createTable(){
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS cache(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        timestamp INTEGER,
                                        query VARCHAR(100) UNIQUE,
                                        result VARCHAR(100))""")
    }

    override fun getByQuery(query: String): CacheEntity? {
        createTable()
        val result = _jdbcTemplate.query("SELECT * FROM cache WHERE query='$query'", _mapper)
        if(result.size==0)
            return null
        else
            return result[0]
    }

    override fun add(item: CacheEntity) {
        createTable()
        _jdbcTemplate.update("DELETE FROM cache WHERE query=?",item.Query)
        _jdbcTemplate.update("""INSERT INTO cache(timestamp, query, result) VALUES(? , ?, ?)""",item.Timestamp, item.Query, item.Result)
    }
}