package com.sd.laborator

import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
class HelloControllerTest(private val embeddedServer: EmbeddedServer) {

    @Test
    fun testServerIsRunning() {
        assert(embeddedServer.isRunning())
    }

    @Test
    fun testIndex() {
        val client: HttpClient = embeddedServer.applicationContext.createBean(HttpClient::class.java, embeddedServer.url)
        assertEquals(HttpStatus.OK, client.toBlocking().exchange("/hello", String::class.java).status())
        client.close()
    }
}
