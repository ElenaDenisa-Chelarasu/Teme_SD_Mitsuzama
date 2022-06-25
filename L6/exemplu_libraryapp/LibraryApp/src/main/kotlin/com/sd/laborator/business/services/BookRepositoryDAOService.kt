package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.models.Book
import com.sd.laborator.persistence.interfaces.IBookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class BookRepositoryDAOService:ILibraryDAOService {
    @Autowired
    private lateinit var _bookRepository: IBookRepository
    override fun getBooks(): Set<Book> {
        return _bookRepository.getAll().toSet()
    }

    override fun addBook(book: Book) {
        _bookRepository.add(book)
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        return _bookRepository.getByAuthor(author).toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return _bookRepository.getByName(title).toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return _bookRepository.getByPublisher(publisher).toSet()
    }
}