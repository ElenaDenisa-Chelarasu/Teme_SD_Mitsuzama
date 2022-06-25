package com.sd.laborator.components

import com.sd.laborator.abstractions.interfaces.LibraryDAO
import com.sd.laborator.abstractions.interfaces.LibraryExtendedPrinter
import com.sd.laborator.model.Book
import com.sd.laborator.model.Content
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Qualifier

@Component
class LibraryAppComponent {
    @Qualifier("advancedLibraryDAO")
    @Autowired
    private lateinit var libraryDAO: LibraryDAO

    @Autowired
    private lateinit var libraryPrinter: LibraryExtendedPrinter

    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent
    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    fun sendMessage(msg: String) {
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(),
                                         connectionFactory.getRoutingKey(),
                                         msg)
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        val processedMsg = (msg.split(",").map { it.toInt().toChar() }).joinToString(separator="")
        val jsonObject=JSONObject(processedMsg)
        val dict=jsonObject.toMap()
        try {
            val function= dict["function"] as String
            val format=dict["format"] as String
            val result: String? = when(function) {
                "print" -> customPrint(format)
                "find" -> customFind(dict["attribute"] as String, dict["value"] as String, format)
                "add" -> {
                    val bookDict=jsonObject.getJSONObject("book").toMap()
                    when(addBook(Book(Content(
                        bookDict["autor"] as String,
                        bookDict["text"] as String,
                        bookDict["nume"] as String,
                        bookDict["editura"] as String)))) {
                        true -> "Carte adaugata cu succes!"
                        false -> "Cartea nu a putut fi adaugata!"
                    }
                }
                else -> null
            }
            if (result != null) sendMessage(result)
        } catch (e: Exception) {
            println(e)
        }
    }

    fun customPrint(format: String): String {
        return when(format) {
            "html" -> libraryPrinter.printHTML(libraryDAO.getBooks())
            "json" -> libraryPrinter.printJSON(libraryDAO.getBooks())
            "raw" -> libraryPrinter.printRaw(libraryDAO.getBooks())
            "xml" -> libraryPrinter.printXML(libraryDAO.getBooks())
            else -> "Not implemented"
        }
    }

    fun customFind(field: String, value: String, printType: String): String {
        val printFunction=when(printType){
            "json" -> LibraryExtendedPrinter::printJSON
            "html" -> LibraryExtendedPrinter::printHTML
            "raw" -> LibraryExtendedPrinter::printRaw
            "xml" -> LibraryExtendedPrinter::printXML
            else -> throw Exception("Invalid print type!")
        }
        return when(field) {
            "author" ->    printFunction(libraryPrinter, libraryDAO.findAllByAuthor(value))
            "title" ->     printFunction(libraryPrinter, libraryDAO.findAllByTitle(value))
            "publisher" -> printFunction(libraryPrinter, libraryDAO.findAllByPublisher(value))
            else -> "Not a valid field"
        }
    }

    fun addBook(book: Book): Boolean {
        return try {
            this.libraryDAO.addBook(book)
            true
        } catch (e: Exception) {
            false
        }
    }

}