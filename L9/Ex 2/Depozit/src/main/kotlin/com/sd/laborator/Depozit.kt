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
open class DepozitMicroservice {
    private val dbManager: IAfacereDBManager = AfacereDBManager()

    private fun confectionareProdusSiAlcatuireColet(id_comanda: Int){
        val comanda = dbManager.getComanda(id_comanda)
        val produs = dbManager.getProduseList().find { it.id==comanda.id_produs }!!
        val stocDisponibil = produs.stoc_disponibil
        val nrMinimDeProduseConfectionate = comanda.cantitate - stocDisponibil
        // It's a miracle
        val stocNouConfectionat = Random.nextInt(nrMinimDeProduseConfectionate, nrMinimDeProduseConfectionate+20)
        println("S-au confectionat $stocNouConfectionat de ${produs.denumire}")
        val stocDupaConfectionareSiAlcatuireColet = stocNouConfectionat + stocDisponibil - comanda.cantitate
        dbManager.addStocProdus(comanda.id_produs, stocDupaConfectionareSiAlcatuireColet)
        println("Produsul ${produs.denumire} in cantitate de ${comanda.cantitate} buc. este pregatit de livrare.")
    }

    private fun alcatuireColet(id_comanda: Int){
        val comanda = dbManager.getComanda(id_comanda)
        val produs = dbManager.getProduseList().find { it.id==comanda.id_produs }!!
        val stocDisponibil = produs.stoc_disponibil

        val stocDupaAlcatuireColet = stocDisponibil - comanda.cantitate
        dbManager.addStocProdus(produs.id, stocDupaAlcatuireColet)
        println("Produsul ${produs.denumire} in cantitate de ${comanda.cantitate} buc. este pregatit de livrare.")
    }

    private fun verificareStoc(id_comanda: Int): Boolean
    {
        val comanda = dbManager.getComanda(id_comanda)
        val stocDisponibil = dbManager.getProduseList()
            .find { it.id==comanda.id_produs }!!.stoc_disponibil
        return stocDisponibil>=comanda.cantitate
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    ///TODO - parametrul ar trebui sa fie doar numarul de inregistrare al comenzii si atat
    fun procesareComanda(comanda: String?): String {
        val id_comanda = comanda!!.toInt()
        println("Procesez comanda cu identificatorul $id_comanda...")
        if(verificareStoc(id_comanda))
            alcatuireColet(id_comanda)
        else
            confectionareProdusSiAlcatuireColet(id_comanda)
        return "$id_comanda"
    }
}
fun main(args: Array<String>) {
    runApplication<DepozitMicroservice>(*args)
}