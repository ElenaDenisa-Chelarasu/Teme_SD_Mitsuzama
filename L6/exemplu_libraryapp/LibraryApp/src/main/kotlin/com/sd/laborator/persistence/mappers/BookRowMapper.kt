package com.sd.laborator.persistence.mappers

import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class BookRowMapper:RowMapper<Book> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Book {
        return Book(
            Content(
                rs.getString("author"),
                rs.getString("text"),
                rs.getString("title"),
                rs.getString("publisher")
            ))
    }
}