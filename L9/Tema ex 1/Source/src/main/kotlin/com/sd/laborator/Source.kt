package com.sd.laborator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.io.File

@EnableBinding(Source::class)
@SpringBootApplication
open class SpringDataFlowTimeSourceApplication {
    private fun getRandomCommand()=File("/home/petru/SD/L9/Tema ex 1/commands.txt").readLines().random()

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller =
    [Poller(fixedDelay = "10000", maxMessagesPerPoll = "1")])
    open fun timeMessageSource(): () -> Message<String> {
        val command = getRandomCommand()
        println("Am ales comanda \"$command\"")
        val map = mapOf("remainingCommands" to command, "lastResult" to "")
        val json = Json.encodeToString(map)
        return { MessageBuilder.withPayload(json).build() }
    }
}

fun main(args: Array<String>) {
    //val command = "lolcat"
    //val aux = " _________________________________________\n" +
    //        "/ O, it is excellent To have a giant's    \\\n" +
    //        "| strength; but it is tyrannous To use it |\n" +
    //        "| like a giant.                           |\n" +
    //        "|                                         |\n" +
    //        "| -- Shakespeare, \"Measure for Measure\",  |\n" +
    //        "\\ II, 2                                   /\n" +
    //        " -----------------------------------------\n" +
    //        "        \\   ^__^\n" +
    //        "         \\  (oo)\\_______\n" +
    //        "            (__)\\       )\\/\\\n" +
    //        "                ||----w |\n" +
    //        "                ||     ||\n"
    //File("aux").writeText(aux)
    //val process = ProcessBuilder(command)
    //    .redirectInput(File("aux"))
    //    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    //    .start()
    ////process.outputStream.bufferedWriter().write(aux)
    ////process.outputStream.write(aux.toByteArray())
    //process.waitFor(10, TimeUnit.SECONDS)
    //val result = process.inputStream.bufferedReader().readText()
    runApplication<SpringDataFlowTimeSourceApplication>(*args)
}