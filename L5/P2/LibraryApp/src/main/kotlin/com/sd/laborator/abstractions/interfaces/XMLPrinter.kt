package com.sd.laborator.abstractions.interfaces

import com.sd.laborator.model.Book

interface XMLPrinter {
    fun printXML(books: Set<Book>): String
}