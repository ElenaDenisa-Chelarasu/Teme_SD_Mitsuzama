package com.sd.laborator.components

import com.sd.laborator.interfaces.CartesianProductOperation
import com.sd.laborator.interfaces.PrimeNumberGenerator
import com.sd.laborator.interfaces.UnionOperation
import com.sd.laborator.model.Stack
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StackAppComponent {
    private var A: Stack? = null
    private var B: Stack? = null

    @Autowired
    private lateinit var primeGenerator: PrimeNumberGenerator
    @Autowired
    private lateinit var cartesianProductOperation: CartesianProductOperation
    @Autowired
    private lateinit var unionOperation: UnionOperation
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
        val result: String? = when(processed_msg) {
            "compute" -> computeExpression()
            "regenerate_A" -> regenerateA()
            "regenerate_B" -> regenerateB()
            else -> null
        }
        println("result: ")
        println(result)
        if (result != null) sendMessage(result)
    }

    fun sendMessage(msg: String) {
        println("message: ")
        println(msg)
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(),
            connectionFactory.getRoutingKey(),
            msg)
    }

    private fun generateStack(count: Int): Stack? {
        if (count < 1)
            return null
        val X: MutableSet<Int> = mutableSetOf()
        while (X.count() < count)
            X.add(primeGenerator.generatePrimeNumber())
        return Stack(X)
    }

    private fun computeExpression(): String {
        if (A == null)
            A = generateStack(20)
        if (B == null)
            B = generateStack(20)
        if (A!!.data.count() == B!!.data.count()) {
            // (A x B) U (B x B)
            val partialResult1 = cartesianProductOperation.executeOperation(A!!.data, B!!.data)
            val partialResult2 = cartesianProductOperation.executeOperation(B!!.data, B!!.data)
            val result = unionOperation.executeOperation(partialResult1, partialResult2)
            return "compute~" + "{\"A\": \"" + A?.data.toString() +"\", \"B\": \"" + B?.data.toString() + "\", \"result\": \"" + result.toString() + "\"}"
        }
        return "compute~" + "Error: A.count() != B.count()"
    }

    private fun regenerateA(): String {
        A = generateStack(20)
        return "A~" + A?.data.toString()
    }

    private fun regenerateB(): String {
        B = generateStack(20)
        return "B~" + B?.data.toString()
    }
}
