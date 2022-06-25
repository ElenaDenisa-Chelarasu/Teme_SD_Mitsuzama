package com.sd.laborator.abstractions.interfaces

import com.sd.laborator.model.Book

interface RawPrinter {
    fun printRaw(books: Set<Book>): String
}