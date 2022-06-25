package com.sd.laborator.services

import com.sd.laborator.abstractions.AbstractLibraryDAOService
import com.sd.laborator.model.Book
import org.springframework.stereotype.Service

@Service
class AdvancedLibraryDAO:AbstractLibraryDAOService() {
    override fun findAllByAuthor(author: String): Set<Book> {
        val filterFunction={it: Book->matchCaseInsensitively(it.author ?:"",author)}
        return getBooks().filter(filterFunction).toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        val filterFunction={it: Book->matchCaseInsensitively(it.name ?:"",title)}
        return getBooks().filter(filterFunction).toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        val filterFunction={it: Book->matchCaseInsensitively(it.publisher ?:"",publisher)}
        return getBooks().filter(filterFunction).toSet()
    }

    private fun matchCaseInsensitively(string: String, pattern:String)=Regex(".*$pattern.*",RegexOption.IGNORE_CASE).matches(string)
}