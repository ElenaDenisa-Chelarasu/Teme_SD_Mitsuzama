package com.sd.laborator;
import com.sd.laborator.rabbitmq.concretes.RabbitMqMessageSender
import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

@FunctionBean("pressButtonFunction")
class PressButtonFunction: FunctionInitializer(), Consumer<PressButtonRequest> {
    private val rabbitMqMessageSender = RabbitMqMessageSender(
        "lab11-tema-ex2.direct",
        "lab11-tema-ex2.routingKeyFromGuiToDataBase")
    private val LOG: Logger = LoggerFactory.getLogger(PressButtonFunction::class.java)
    override fun accept(t: PressButtonRequest) {
        val buttonName = t.getButton()!!
        LOG.info("S-a apasat butonul $buttonName")
        rabbitMqMessageSender.send(buttonName)
    }
}