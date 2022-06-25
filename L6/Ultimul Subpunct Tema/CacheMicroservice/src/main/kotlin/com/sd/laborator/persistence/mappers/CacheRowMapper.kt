package com.sd.laborator.persistence.mappers

import com.sd.laborator.persistence.entities.CacheEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet


class CacheRowMapper: RowMapper<CacheEntity> {
    override fun mapRow(rs: ResultSet, rowNum: Int): CacheEntity? {
        try {
            return CacheEntity(
                rs.getInt("id"),
                rs.getInt("timestamp"),
                rs.getString("query"),
                rs.getString("result")
            )
        }catch(e: Exception){
            return null
        }
    }
}