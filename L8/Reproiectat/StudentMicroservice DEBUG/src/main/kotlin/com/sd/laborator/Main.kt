package com.sd.laborator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    val json = """{
        |"type":"tip",
        |"destinationType":"destTip",
        |"responseType":"responseTypeee",
        |"destination":"destinationn",
        |"source":"sourceee",
        |"content":"WWWWW"}""".trimMargin()
    val message = Message.fromJson(json)
    val map = Json.decodeFromString<Map<String, String>>(message.toJson())
    println("$#$#@$@#$@")
}
