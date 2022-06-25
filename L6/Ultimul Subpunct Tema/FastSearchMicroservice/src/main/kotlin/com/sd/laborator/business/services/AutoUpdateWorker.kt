package com.sd.laborator.business.services

import com.sd.laborator.business.abstractClasses.AbstractAutoUpdateWorker
import com.sd.laborator.business.helpers.CacheItemRowMapper
import com.sd.laborator.business.interfaces.IFastSearchDAO
import com.sd.laborator.business.models.CacheItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service

@Service
open class AutoUpdateWorker(override val _fastSearchDAO: IFastSearchDAO): AbstractAutoUpdateWorker() {
    @Value("\${autoUpdateInterval}")
    override val _autoUpdateInterval: Int = 3

    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate

    private val _rowMapper : RowMapper<CacheItem> = CacheItemRowMapper()

    override fun run() {
        println(_fastSearchDAO.hashCode())
        var cacheItemList: List<CacheItem>
        while(true){
            println("Autoupdating tree...")
            cacheItemList = _jdbcTemplate.query("SELECT * FROM cache", _rowMapper)
            _fastSearchDAO.reset()
            cacheItemList.forEach { println(it.query);_fastSearchDAO.insert(it) }
            Thread.sleep(_autoUpdateInterval.toLong() * 1000L)
        }
    }
}