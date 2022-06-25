package com.sd.laborator

import com.sd.laborator.components.MonitorizableComponent
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    // val json = """{
    //     |"type":"tip",
    //     |"destinationType":"destTip",
    //     |"responseType":"responseTypeee",
    //     |"destination":"destinationn",
    //     |"source":"sourceee",
    //     |"content":"WWWWW"}""".trimMargin()
    // val message = Message.fromJson(json)
    // message.content="WW\"aa\"WW:Xx"
    // val map = Json.decodeFromString<Map<String, String>>(message.toJson())
    // println("$#$#@$@#$@")

    val monitorizableComponent = MonitorizableComponent(50)
    monitorizableComponent.run()
}
