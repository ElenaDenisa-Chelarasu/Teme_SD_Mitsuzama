package com.sd.laborator.persistence.interfaces

import com.sd.laborator.business.models.Book

interface IBookRepository {
    // Create
    fun createTable()
    fun add(book: Book)

    // Retrieve
    fun getAll(): List<Book>
    fun getByName(name: String): List<Book>
    fun getByAuthor(author: String): List<Book>
    fun getByPublisher(publisher: String): List<Book>
}