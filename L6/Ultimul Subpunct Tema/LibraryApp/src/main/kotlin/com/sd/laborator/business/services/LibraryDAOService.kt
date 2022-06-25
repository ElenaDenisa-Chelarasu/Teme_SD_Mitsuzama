package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import org.springframework.stereotype.Service

@Service
class LibraryDAOService : ILibraryDAOService {
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

    override fun getBooks(): Set<Book> {
        return this._books
    }

    override fun addBook(book: Book) {
        this._books.add(book)
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        return (this._books.filter { it.hasAuthor(author) }).toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return (this._books.filter { it.hasTitle(title) }).toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return (this._books.filter { it.publishedBy(publisher) }).toSet()
    }
}