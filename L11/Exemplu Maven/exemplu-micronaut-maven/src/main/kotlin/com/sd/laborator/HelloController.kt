package com.sd.laborator
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpStatus
@Controller("/hello")
class HelloController {
    @Get(uri="/", produces=["text/plain"])
    fun index(): String {
        return "Hello from Micronaut!"
    }
}