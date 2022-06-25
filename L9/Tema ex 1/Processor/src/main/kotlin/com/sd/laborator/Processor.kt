package com.sd.laborator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import java.io.File

@EnableBinding(Processor::class)
@SpringBootApplication
open class PipelinedCommandProcessor {
    @Transformer(inputChannel = Processor.INPUT, outputChannel =
    Processor.OUTPUT)
    fun transform(json: String?): String {
        val map = Json.decodeFromString<Map<String, String>>(json!!)
        val lastResult = map["lastResult"]!!
        val commands = map["remainingCommands"]!!.split("|")
        val currentCommand = commands[0]
        println("Execut comanda $currentCommand")
        var processBuilder =
            ProcessBuilder(currentCommand)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
        if(lastResult.isNotEmpty()) {
            println("Rezultatul comezilor anterioare:\n$lastResult")
            File("/home/petru/SD/L9/Tema ex 1/lastResult").writeText(lastResult)
            processBuilder=processBuilder.redirectInput(File("/home/petru/SD/L9/Tema ex 1/lastResult"))
        }
        val process = processBuilder.start()
        val result = process.inputStream.bufferedReader().readText()
        val remainingCommands = commands.drop(1).joinToString("|")
        val newMap = mapOf("lastResult" to result, "remainingCommands" to remainingCommands)
        return Json.encodeToString(newMap)
    }
}

fun main(args: Array<String>) {
    val json = "{\"lastResult\":\"QQQQWWWWAASDASDAS\",\"remainingCommands\":\"cowsay|lolcat\"}"
    val map = Json.decodeFromString<Map<String, String>>(json!!)
    val lastResult = map["lastResult"]!!
    val commands = map["remainingCommands"]!!.split("|")
    val currentCommand = commands[0]
    println("Execut comanda $currentCommand")
    var processBuilder =
        ProcessBuilder(currentCommand)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
    if(lastResult.isNotEmpty()) {
        println("Rezultatul comezilor anterioare:\n$lastResult")
        File("lastResult").writeText(lastResult)
        processBuilder=processBuilder.redirectInput(File("lastResult"))
    }
    val process = processBuilder.start()
    val result = process.inputStream.bufferedReader().readText()
    val remainingCommands = commands.drop(1).joinToString("|")
    val newMap = mapOf("lastResult" to result, "remainingCommands" to remainingCommands)
    runApplication<PipelinedCommandProcessor>(*args)
}