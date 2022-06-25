package com.sd.laborator
import dbManagers.concretes.AfacereDBManager
import dbManagers.interfaces.IAfacereDBManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import kotlin.random.Random
@EnableBinding(Source::class)
@SpringBootApplication
open class ClientMicroservice {
    private val dbManager: IAfacereDBManager = AfacereDBManager()

    private fun getRandomClientId()=dbManager.getClientiList().random().id_client

    private fun getRandomProdusId()=dbManager.getProduseList().random().id

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller =
    [Poller(fixedDelay = "10000", maxMessagesPerPoll = "1")])
    open fun comandaProdus(): () -> Message<String> {
        return {
            val id_produs = getRandomProdusId()
            val id_client = getRandomClientId()
            val cantitate: Int = Random.nextInt(1, 100)
            val mesaj =
                "$id_client|$id_produs|$cantitate"
            MessageBuilder.withPayload(mesaj).build()
        }
    }
}
fun main(args: Array<String>) {
    val dbManager = AfacereDBManager()
    val clienti = dbManager.getClientiList()
    runApplication<ClientMicroservice>(*args)
}