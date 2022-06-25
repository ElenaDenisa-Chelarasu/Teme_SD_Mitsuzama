package com.sd.laborator.components

import com.sd.laborator.dbManagers.concretes.ButtonsDBManager
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ButtonClickComponent {
    val dbManager = ButtonsDBManager("/home/petru/SD/L11/Tema/Ex 2-3/database.db")
    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${stackapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        val buttonName = String(msg.split(",").map { it.toInt().toByte() }.toByteArray())
        println("Incrementing $buttonName clicks...")
        dbManager.incrementButtonClicks(buttonName)
    }
}
