package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ICachingService
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class CachingController {
    @Autowired
    private lateinit var _cachingService:ICachingService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${cache.rabbitmq.queueFromGuiToCache}"])
    fun receiveMessageFromGui(msg: String) {
        // val map= Json.decodeFromString<Map<String, String>>(msg)
        // val newMsg = map.map { "${it.key}->${it.value}" }.joinToString(",")
        println("Message received from Gui: $msg")
        val cachedResult=_cachingService.exists(msg)
        if(cachedResult!=null)
            sendMessageToGui(cachedResult)
        else
            sendMessageToLibrary(msg)
    }

    @RabbitListener(queues = ["\${cache.rabbitmq.queueFromLibraryToCache}"])
    fun receiveMessageFromLibrary(msg: String) {
        println("Message received from Library: $msg")
        val map= Json.decodeFromString<Map<String, String>>(msg)
        _cachingService.addToCache(map["query"]!!,map["result"]!!)
        sendMessageToGui(map["result"]!!)
    }

    fun sendMessageToLibrary(msg: String) {
        println("Request forwarded to library: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyFromCacheToLibrary(), msg)
    }

    fun sendMessageToGui(msg: String) {
        println("Response for Gui: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyFromCacheToGui(), msg)
    }
}