package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IFastSearchDAOAdapter
import com.sd.laborator.business.services.CacheItemToAndFromJsonSerializer
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import kotlinx.serialization.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class FastSearchController {
    @Autowired
    private lateinit var _fastSearchDAO: IFastSearchDAOAdapter

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${rabbitmq.queueFromMaestro}"])
    fun receiveRabbitMessage(msg: String) {
        //Returnare rezultat din arbore in caz ca se gaseste sau returnare mesaj primit
        println("Message received from Maestro: $msg")
        _fastSearchDAO.get(msg)?.let { sendRabbitMessage(it) }?:sendRabbitMessage(msg)
    }
    //Message received from Maestro: {"function":"find","attribute":"title","value":"Programming in LUA","format":"json"}
    //1. Gasit:
    // Response for Maestro: {"query":"{\"function\":\"find\",\"attribute\":\"title\",\"value\":\"Programming in LUA\",\"format\":\"json\"}","result":"[\n    {\"Titlu\": \"Programming in LUA\", \"Autor\":\"Roberto Ierusalimschy\", \"Editura\":\"Teora\", \"Text\":\"Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...\"}\n]\n","timestamp":"1648730275"}
    //2. Nu a fost gasit:
    // Response for Maestro: {"function":"find","attribute":"title","value":"Programming in LUA","format":"json"}
    fun sendRabbitMessage(msg: String) {
        println("Response for Maestro: $msg")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyToMaestro(), msg)
    }
}