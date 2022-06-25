package com.sd.laborator.business.helpers

import com.sd.laborator.business.models.CacheItem
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class CacheItemRowMapper:RowMapper<CacheItem> {
    override fun mapRow(rs: ResultSet, rowNum: Int): CacheItem? {
        return CacheItem(rs.getString("query"), rs.getString("result"), rs.getInt("timestamp"))
    }
}