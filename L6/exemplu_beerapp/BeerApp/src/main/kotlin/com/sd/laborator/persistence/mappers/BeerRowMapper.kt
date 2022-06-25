package com.sd.laborator.persistence.mappers

import com.sd.laborator.models.Beer
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class BeerRowMapper : RowMapper<Beer?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Beer {
        return Beer(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"))
    }
}