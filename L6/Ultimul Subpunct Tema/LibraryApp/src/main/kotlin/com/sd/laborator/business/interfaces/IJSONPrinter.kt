package com.sd.laborator.business.interfaces

import com.sd.laborator.business.models.Book

interface IJSONPrinter {
    fun printJSON(books: Set<Book>): String
}