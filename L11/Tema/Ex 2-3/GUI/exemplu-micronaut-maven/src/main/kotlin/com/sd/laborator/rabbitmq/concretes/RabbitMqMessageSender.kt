package com.sd.laborator.rabbitmq.concretes

import com.rabbitmq.client.ConnectionFactory

class RabbitMqMessageSender(private val exchange: String, private val routingKey: String) {
    fun send(message: String){
        val factory = ConnectionFactory()
        factory.setUri("amqp://student:student@localhost:5672")
        val con = factory.newConnection()
        val channel = con.createChannel()
        val bytes = message.toByteArray()
        channel.basicPublish(exchange, routingKey, null, bytes)
        channel.close()
        con.close()
    }
}