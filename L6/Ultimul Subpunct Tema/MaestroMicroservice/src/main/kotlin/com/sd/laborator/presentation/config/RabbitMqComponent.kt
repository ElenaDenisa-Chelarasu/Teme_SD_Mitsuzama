package com.sd.laborator.presentation.config

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RabbitMqComponent {
    @Value("\${spring.rabbitmq.host}")
    private lateinit var host: String
    @Value("\${spring.rabbitmq.port}")
    private val port: Int = 0
    @Value("\${spring.rabbitmq.username}")
    private lateinit var username: String
    @Value("\${spring.rabbitmq.password}")
    private lateinit var password: String
    @Value("\${rabbitmq.exchange}")
    private lateinit var exchange: String
    @Value("\${rabbitmq.routingKeyToGui}")
    private lateinit var routingKeyToGui: String
    @Value("\${rabbitmq.routingKeyToFastSearch}")
    private lateinit var routingKeyToFastSearch: String
    @Value("\${rabbitmq.routingKeyToCache}")
    private lateinit var routingKeyToCache: String
    @Value("\${rabbitmq.routingKeyToLibrary}")
    private lateinit var routingKeyToLibrary: String

    fun getExchange(): String = this.exchange

    fun getRoutingKeyToGui(): String = routingKeyToGui
    fun getRoutingKeyToFastSearch(): String = routingKeyToFastSearch
    fun getRoutingKeyToCache(): String = routingKeyToCache
    fun getRoutingKeyToLibrary(): String = routingKeyToLibrary





    @Bean
    private fun connectionFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory()
        connectionFactory.host = this.host
        connectionFactory.username = this.username
        connectionFactory.setPassword(this.password)
        connectionFactory.port = this.port
        return connectionFactory
    }

    @Bean
    fun rabbitTemplate(): RabbitTemplate = RabbitTemplate(connectionFactory())

}