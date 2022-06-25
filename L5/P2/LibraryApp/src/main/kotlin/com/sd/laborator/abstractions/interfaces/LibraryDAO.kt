package com.sd.laborator.abstractions.interfaces

import com.sd.laborator.model.Book

interface LibraryDAO {
    fun getBooks(): Set<Book>
    fun addBook(book: Book)
    fun findAllByAuthor(author: String): Set<Book>
    fun findAllByTitle(title: String): Set<Book>
    fun findAllByPublisher(publisher: String): Set<Book>
}