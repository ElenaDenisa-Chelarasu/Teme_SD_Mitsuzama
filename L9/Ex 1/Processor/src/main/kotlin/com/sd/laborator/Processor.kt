package com.sd.laborator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import java.text.DateFormat
import java.text.SimpleDateFormat

@EnableBinding(Processor::class)
@SpringBootApplication
open class SpringDataFlowTimeProcessorApplication {
    @Transformer(inputChannel = Processor.INPUT, outputChannel =
    Processor.OUTPUT)
    fun transform(timestamp: Long?): Any? {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            return dateFormat.format(timestamp)
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataFlowTimeProcessorApplication>(*args)
}