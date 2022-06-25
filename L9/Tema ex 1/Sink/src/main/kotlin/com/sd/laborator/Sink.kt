package com.sd.laborator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
@SpringBootApplication
open class SpringDataFlowTimeSinkApplication {
    @StreamListener(Sink.INPUT)
    fun loggerSink(json: String) {
        val map = Json.decodeFromString<Map<String, String>>(json)
        if(map["remainingCommands"] != "")
            println("Comanda pipeline-ul a avut prea multe comenzi. Nu s-au putut executa ${map["remainingCommands"]}...")
        println("Rezultat final:\n${map["lastResult"]}")
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataFlowTimeSinkApplication>(*args)
}