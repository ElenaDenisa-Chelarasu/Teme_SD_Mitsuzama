package com.sd.laborator

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KotlinConsumer {
    @KafkaListener(topics = ["topic_exemplu_kotlin"], groupId = "exemplu-consumer-kotlin")
    fun processMessage(message: String) {
        println("Am consumat urmatorul mesaj: $message")
    }
}