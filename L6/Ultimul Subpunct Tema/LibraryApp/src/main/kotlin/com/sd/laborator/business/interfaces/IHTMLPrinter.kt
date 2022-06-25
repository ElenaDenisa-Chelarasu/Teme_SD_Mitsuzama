package com.sd.laborator.business.interfaces

import com.sd.laborator.business.models.Book

interface IHTMLPrinter {
    fun printHTML(books: Set<Book>): String
}