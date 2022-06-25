package com.sd.laborator

import com.rabbitmq.client.ConnectionFactory
import com.sd.laborator.dbManagers.concretes.ButtonsDBManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class TestClass {
    @Test
    fun test() {
        // {"Buton 1":3,"Buton 2":7,"Buton 3":15}
        val factory = ConnectionFactory()
        factory.setUri("amqp://student:student@localhost:5672")
        val con = factory.newConnection()
        val channel = con.createChannel()
        val queueName = "lab11-tema-ex2-FromGuiToDataBase"
        val exchange = "lab11-tema-ex2.direct"
        val routingKey = "lab11-tema-ex2.routingKeyFromGuiToDataBase"
        val bytes = """{ "ABC": 1234 }""".toByteArray()
        channel.basicPublish(exchange, routingKey, null, bytes)
        channel.close()
        con.close()
    }
}