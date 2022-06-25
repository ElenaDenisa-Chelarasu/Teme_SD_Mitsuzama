package com.sd.laborator.presentation.controllers

import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class MaestroController {
    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${rabbitmq.queueFromGui}"])
    fun receiveFromGui(msg: String) {
        println("From Gui: $msg")
        sendToFastSearch(msg)
    }

    @RabbitListener(queues = ["\${rabbitmq.queueFromFastSearch}"])
    fun receiveFromFastSearch(msg: String) {
        println("From FastSearch: $msg")
        Json.decodeFromString<Map<String, String>>(msg)["result"]?.let {
            // Raspuns gasit in FastSearch
            println("Found in FastSearch!")
            sendToGui(it)
        }?: run {
            println("Not found in FastSearch!")
            // Raspuns negasit in FastSearch
            // Cerere inaintata spre Cache
            sendToCache(msg)
        }
    }

    @RabbitListener(queues = ["\${rabbitmq.queueFromCache}"])
    fun receiveFromCache(msg: String) {
        println("From Cache: $msg")
        Json.decodeFromString<Map<String, String>>(msg)["result"]?.let {
            // Raspuns gasit in Cache
            println("Found in Cache!")
            sendToGui(it)
        }?: run {
            // Raspuns negasit in Cache
            // Cerere inaintata spre Library
            println("Not found in Cache!")
            sendToLibrary(msg)
        }
    }

    @RabbitListener(queues = ["\${rabbitmq.queueFromLibrary}"])
    fun receiveFromLibrary(msg: String) {
        println("From Library: $msg")
        Json.decodeFromString<Map<String, String>>(msg)["result"]?.let {
            println("Updating Cache...")
            sendToCache(msg)
            sendToGui(it)
        }?: run {
            println("Invalid response from Library!")
            sendToGui("Invalid response from Library!")
        }
    }


    fun sendToGui(msg: String) {
        println("To Gui: $msg\n\n\n")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToGui(), msg)
    }

    fun sendToFastSearch(msg: String) {
        println("To FastSearch: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToFastSearch(), msg)
    }

    fun sendToCache(msg: String) {
        println("To Cache: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToCache(), msg)
    }

    fun sendToLibrary(msg: String) {
        println("To Library: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToLibrary(), msg)
    }
}