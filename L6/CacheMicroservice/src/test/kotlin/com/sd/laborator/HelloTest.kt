package com.sd.laborator

import com.sd.laborator.persistence.repositories.CachingRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class HelloTest {
}

fun main(args: Array<String>){
    val cache=CachingRepository()
    val ok = cache.getByQuery("{\"function\":\"print\",\"format\":\"json\"}")
    val json= Json.decodeFromString<Map<String, String>>("{\"name\":\"abcd\", \"id\":\"1234\"}")
}
