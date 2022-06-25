package com.sd.laborator.components

import com.sd.laborator.abstractions.abstractClasses.ResponsabilityChainNode
import com.sd.laborator.model.Stack
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StackAppComponent {
    @Autowired
    private lateinit var operationChainNode: ResponsabilityChainNode

    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${stackapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        // the result: 114,101,103,101,110,101,114,97,116,101,95,65 --> needs processing
        val processed_msg = (msg.split(",").map { it.toInt().toChar() }).joinToString(separator="")
        val result: String = operationChainNode.doTask(processed_msg)
        println("result: ")
        println(result)
        sendMessage(result)
    }

    fun sendMessage(msg: String) {
        println("message: ")
        println(msg)
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(),
            connectionFactory.getRoutingKey(),
            msg)
    }
}
