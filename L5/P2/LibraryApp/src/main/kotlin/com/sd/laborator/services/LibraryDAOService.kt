package com.sd.laborator.services

import com.sd.laborator.abstractions.AbstractLibraryDAOService
import com.sd.laborator.abstractions.interfaces.LibraryDAO
import com.sd.laborator.model.Book
import com.sd.laborator.model.Content
import org.springframework.stereotype.Service

@Service
class LibraryDAOService: AbstractLibraryDAOService() {

    override fun findAllByAuthor(author: String): Set<Book> {
        return (getBooks().filter { it.hasAuthor(author) }).toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return (getBooks().filter { it.hasTitle(title) }).toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return (getBooks().filter { it.publishedBy(publisher) }).toSet()
    }
}