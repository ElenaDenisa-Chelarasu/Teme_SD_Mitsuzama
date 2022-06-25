package com.sd.laborator
import dbManagers.concretes.AfacereDBManager
import dbManagers.interfaces.IAfacereDBManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import kotlin.random.Random

@EnableBinding(Processor::class)
@SpringBootApplication
open class ComandaMicroservice {
    private val dbManager: IAfacereDBManager = AfacereDBManager()

    @Transformer(inputChannel = Processor.INPUT, outputChannel =
    Processor.OUTPUT)
    fun preluareComanda(comanda: String?): String {
        val (id_client, id_produs, cantitate) = comanda!!.split("|")
        println("Am primit comanda urmatoare: ")
        println("$id_client | $id_produs | $cantitate")
        val idComanda = dbManager.insertComanda(id_client.toInt(), id_produs.toInt(), cantitate.toInt())
        return "$idComanda"
    }
}
fun main(args: Array<String>) {
    runApplication<ComandaMicroservice>(*args)
}