package com.sd.laborator

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

data class Message(
    // @JvmField pentru ca by default Java va vedea proprietatile expuse de Kotlin ca fiind private...
    // Cu adnotarea de @JvmField le va vedea ca publice, precum sunt defapt
    @JvmField var type: String = "",
    @JvmField var destinationType: String = "",
    @JvmField var responseType: String = "",
    @JvmField var destination: String = "",
    @JvmField var content: String = "",
    @JvmField var source: String = "") {
    fun toJson(): String {
        val content =
            Message::class.java.declaredFields
                .asSequence()
                .filterNot { it.name == "Companion" }
                .map { "\"${it.name}\":\"${it.get(this)}\"" }
                .joinToString(separator = ",")
        return "{$content}"
    }

    companion object {
        fun fromJson(json: String): Message {
            val map = Json.decodeFromString<Map<String, String>>(json)
            val result = Message()
            // Obtinere "clasa"
            Message::class.java.
            // Obtinere campuri declarate
            declaredFields
                    // Eliminarea campului Companion
                .filterNot { it.name == "Companion" }
                    // Pentru fiecare camp setez valoarea valoarea sa din obiectul la care refera result
                    // cu valoarea asociata cheii cu acelasi nume din Map-ul rezultat din parsarea JSON-ului
                .forEach {
                    it.set(result, map[it.name]!!)
                }
            return result
        }
    }
}