package com.sd.laborator.abstractions.interfaces

import com.sd.laborator.model.Book

interface JSONPrinter {
    fun printJSON(books: Set<Book>): String
}