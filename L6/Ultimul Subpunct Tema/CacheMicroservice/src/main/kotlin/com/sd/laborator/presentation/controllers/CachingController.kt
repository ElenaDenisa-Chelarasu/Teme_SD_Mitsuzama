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

    //1. Insert in cache: de la Maestro: {"query":"...","result":"..."}
    //2. Get din cache: de la Maestro: {...}
    @RabbitListener(queues = ["\${rabbitmq.queueFromMaestro}"])
    fun receiveRabbitMessage(msg: String) {
        println("Message from Maestro: $msg")
        val map= Json.decodeFromString<Map<String, String>>(msg)
        val query = map["query"]
        val result = map["result"]
        if(result!=null && query!=null){
            _cachingService.addToCache(query, result)
            println("Item added to cache...\n\n\n")
        }
        else _cachingService.exists(msg)?.let {
            val responseMap = mapOf("query" to msg, "result" to it)
            sendRabbitMessage(Json.encodeToString(responseMap))
        }?: run {
            println("Not found")
            sendRabbitMessage(msg)
        }
    }

    fun sendRabbitMessage(msg: String) {
        println("Message for Maestro: $msg\n\n\n\n")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToMaestro(), msg)
    }
}