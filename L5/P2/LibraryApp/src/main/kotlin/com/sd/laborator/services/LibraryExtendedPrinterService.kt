package com.sd.laborator.services

import com.sd.laborator.abstractions.interfaces.LibraryExtendedPrinter
import com.sd.laborator.model.Book
import org.springframework.stereotype.Service

@Service
class LibraryExtendedPrinterService: LibraryExtendedPrinter,LibraryPrinterService() {
    override fun printXML(books: Set<Book>): String {
        val content = StringBuilder("<books>")
        books.forEach {
            content.append("\n\t<book>")
            content.append("\n\t\t<nume>${it.name}</nume>")
            content.append("\n\t\t<autor>${it.author}</autor>")
            content.append("\n\t\t<editura>${it.publisher}</editura>")
            content.append("\n\t\t<continut>${it.content}</continut>")
            content.append("\n\t</book>")
        }
        return content.append("\n</books>").toString()
    }
}