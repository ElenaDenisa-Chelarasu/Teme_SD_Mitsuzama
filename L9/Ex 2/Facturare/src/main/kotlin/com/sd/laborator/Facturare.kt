package com.sd.laborator
import dbManagers.concretes.AfacereDBManager
import dbManagers.interfaces.IAfacereDBManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import kotlin.math.abs
import kotlin.random.Random
@EnableBinding(Processor::class)
@SpringBootApplication
open class FacturareMicroservice {
    private val dbManager: IAfacereDBManager = AfacereDBManager()

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun emitereFactura(comanda: String?): String {
        println("Emit factura pentru comanda $comanda...")
        val nrFactura = dbManager.insertFactura(comanda!!.toInt())
        println("S-a emis factura cu nr $nrFactura.")
        return "$comanda"
    }
}
fun main(args: Array<String>) {
    runApplication<FacturareMicroservice>(*args)
}