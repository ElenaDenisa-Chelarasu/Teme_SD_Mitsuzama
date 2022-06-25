package com.sd.laborator;
import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import khttp.get
import khttp.post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@FunctionBean("ProducerFunction")
class ProducerFunction : FunctionInitializer(), Supplier<ProducerResponse> {
    private val LOG: Logger = LoggerFactory.getLogger(ProducerFunction::class.java)
    override fun get(): ProducerResponse {
        val response = ProducerResponse()
        LOG.info("S-a apelat ProducerFunction.")
        val getResponse = get("https://xkcd.com/atom.xml")
        LOG.info("S-a obtinut resursa XML prin apelul metodei GET.")
        val headers = mapOf("Content-Type" to "application/json")
        val map = mapOf("xmlData" to getResponse.text)
        val postResponse = post("http://localhost:8080/", headers, data = Json.encodeToString(map))
        LOG.info("S-a trimis cererea POST!")
        response.setMessage("OK")
        return response
    }
}

fun main(args : Array<String>) {
    val function = ProducerFunction()
    function.run(args) {
        function.get()
    }
}
