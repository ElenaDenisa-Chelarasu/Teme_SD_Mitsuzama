package com.sd.laborator
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }
    @Controller
    class LambdaController {
        @Get
        fun execute():
                ProducerResponse {
            return handler.get()
        }
        companion object {
            private val handler = ProducerFunction()
        }
    }
}