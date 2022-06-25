package com.sd.laborator.persistence.repository

import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.persistence.interfaces.IBookRepository
import com.sd.laborator.persistence.mappers.BookRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class BookRepository:IBookRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<Book> = BookRowMapper()
    private var _books: MutableSet<Book> = mutableSetOf(
        Book(
            Content(
                "Roberto Ierusalimschy",
                "Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...",
                "Programming in LUA",
                "Teora"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ....",
                "Steaua Sudului",
                "Corint"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte.",
                "O calatorie spre centrul pamantului",
                "Polirom"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ...",
                "Insula Misterioasa",
                "Teora"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ...",
                "Casa cu aburi",
                "Albatros"
            )
        )
    )

    override fun createTable() {
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS books(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        author VARCHAR(100),
                                        title VARCHAR(100) UNIQUE,
                                        publisher VARCHAR(100),
                                        text VARCHAR(100) UNIQUE
                                        )""")
        val count = _jdbcTemplate.query("SELECT COUNT(*) FROM books",
            RowMapper{resultSet, i -> resultSet.getInt("COUNT(*)") })[0]
        if(count==0){
            _books.forEach {
                _jdbcTemplate.update("INSERT INTO books(author, title, publisher, text) VALUES (?, ?, ?, ?)",
            it.author, it.name, it.publisher, it.content
                )}
        }
    }

    override fun add(book: Book) {
        try {
            createTable()
            _jdbcTemplate.update("INSERT INTO books(author, title, publisher, text) VALUES (?, ?, ?, ?)",
                book.author, book.name, book.publisher, book.content)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }

    override fun getAll(): List<Book> {
        createTable()
        return _jdbcTemplate.query("SELECT * FROM books", _rowMapper)
    }

    override fun getByName(name: String): List<Book> {
        createTable()
        val result = _jdbcTemplate.query("SELECT * FROM books WHERE title = '$name'", _rowMapper)
        return when(result){
            null -> listOf(Book(Content("", "", "", "")))
            else->result
        }
    }

    override fun getByAuthor(author: String): List<Book> {
        createTable()
        val result = _jdbcTemplate.query("SELECT * FROM books WHERE author = '$author'", _rowMapper)
        return when(result){
            null -> listOf(Book(Content("", "", "", "")))
            else->result
        }
    }

    override fun getByPublisher(publisher: String): List<Book> {
        createTable()
        val result = _jdbcTemplate.query("SELECT * FROM books WHERE publisher = '$publisher'", _rowMapper)
        return when(result){
            null -> listOf(Book(Content("", "", "", "")))
            else->result
        }
    }
}