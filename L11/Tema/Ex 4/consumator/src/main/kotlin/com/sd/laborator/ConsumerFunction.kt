package com.sd.laborator;
import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

@FunctionBean("ConsumerFunction")
class ConsumerFunction : FunctionInitializer(), Consumer<ConsumerRequest> {
    private val LOG: Logger = LoggerFactory.getLogger(ConsumerFunction::class.java)
    override fun accept(t: ConsumerRequest) {
        val text = t.getXmlData()!!
        val titleRegex = Regex("(?<=<title>)[^<]*(?=<\\/title>)")
        val linkRegex = Regex("(?<=<link href=\")[^\"]*(?=\" rel=\"alternate\">)")
        val titles = titleRegex.findAll(text).map { it.value }
        val links = linkRegex.findAll(text).map { it.value }
        val titlesToLinks = titles.zip(links)
        titlesToLinks.forEach { LOG.info("Titlu: ${it.first} -> Link: ${it.second}") }
    }
}

fun main(args : Array<String>) {
    val function = ConsumerFunction()
    function.run(args) {
        function.accept(it.get(ConsumerRequest::class.java))
    }
}
