package com.sd.laborator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
@SpringBootApplication
open class SpringDataFlowTimeSinkApplication {
    @StreamListener(Sink.INPUT)
    fun loggerSink(date: String) {
        println("Am primit urmatorul mesaj: $date")
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataFlowTimeSinkApplication>(*args)
}