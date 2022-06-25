package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import com.sd.laborator.presentation.config.RabbitMqComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LibraryPrinterController {
    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }


    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        return when (format) {
            "html" -> _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
            "json" -> _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
            "raw" -> _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
            else -> "Not implemented"
        }
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ): String {
        if (author != "")
            return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByAuthor(author))
        if (title != "")
            return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByTitle(title))
        if (publisher != "")
            return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByPublisher(publisher))
        return "Not a valid field"
    }
    @RequestMapping("/find-and-print", method = [RequestMethod.GET])
    @ResponseBody
    fun customFindAndPrint(
        @RequestParam(required=true, name="attributeName") attributeName: String,
        @RequestParam(required=true, name="attributeValue") attributeValue: String,
        @RequestParam(required=false, name="format", defaultValue = "json") format: String
    ): String {
        val attributeNameToFindFunction=mapOf(
            "author" to ILibraryDAOService::findAllByAuthor,
            "title" to ILibraryDAOService::findAllByTitle,
            "publisher" to ILibraryDAOService::findAllByPublisher
        )
        if(attributeName !in attributeNameToFindFunction.keys)
            return "Invalid attribute name!"

        val formatToPrintFunction=mapOf(
            "json" to ILibraryPrinterService::printJSON,
            "html" to ILibraryPrinterService::printHTML,
            "raw" to ILibraryPrinterService::printRaw
        )
        if(format !in formatToPrintFunction.keys)
            return "Invalid format!"

        val printFunction = formatToPrintFunction[format]!!
        val findFunction = attributeNameToFindFunction[attributeName]!!
        return printFunction(_libraryPrinterService, findFunction(_libraryDAOService, attributeValue).toSet())
    }

    @RabbitListener(queues = ["\${rabbitmq.queueFromMaestro}"])
    fun receiveRabbitMessage(msg: String) {
        println("Message from Maestro: $msg")
        val map = Json.decodeFromString<Map<String,String>>(msg)
        var result: String
        if(map["function"]=="print"){
            result = customPrint(map["format"]?:"json")
        }else if(map["function"]=="find"){
            try{
                result = customFindAndPrint(map["attribute"]!!, map["value"]!!, map["format"]!!)
            }catch(e: java.lang.NullPointerException){
                result = customPrint("json")
            }
        }else{
            result = customPrint("json")
        }
        val response="{\"query\":\"${escapeString(msg)}\",\"result\":\"${escapeString(result)}\"}"
        sendRabbitMessage(response)
    }

    fun sendRabbitMessage(msg: String) {
        println("Response for Maestro: \"$msg\"\n\n\n")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToMaestro(), msg)
    }

    private fun escapeString(string: String): String {
        return string
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}