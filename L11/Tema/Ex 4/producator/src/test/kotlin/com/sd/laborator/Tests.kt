package com.sd.laborator

import khttp.get
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(){
    val getResponse = get("https://xkcd.com/atom.xml")
    val text = getResponse.text
    val map = mapOf("xmlData" to text)
    val json = Json.encodeToString(map)
    val newMap = Json.decodeFromString<Map<String, String>>(json)
    val titleRegex = Regex("(?<=<title>)[^<]*(?=<\\/title>)")
    val linkRegex = Regex("(?<=<link href=\")[^\"]*(?=\" rel=\"alternate\">)")
    val matches = linkRegex.findAll(text)
    val i=0
}