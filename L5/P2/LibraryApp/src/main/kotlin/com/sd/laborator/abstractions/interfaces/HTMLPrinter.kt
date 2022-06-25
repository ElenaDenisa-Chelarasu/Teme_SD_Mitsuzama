package com.sd.laborator.abstractions.interfaces

import com.sd.laborator.model.Book

interface HTMLPrinter {
    fun printHTML(books: Set<Book>): String
}