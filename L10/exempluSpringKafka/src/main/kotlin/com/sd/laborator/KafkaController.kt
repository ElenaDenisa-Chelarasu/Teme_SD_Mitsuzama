package com.sd.laborator

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class KafkaController(private val kotlinProducer: KotlinProducer) {
    @RequestMapping(path = ["/publish"], method = [RequestMethod.PUT])
    @ResponseBody
    fun publishMessage(@RequestBody message: String): ResponseEntity<HttpStatus> {
        kotlinProducer.send(message)
        return ResponseEntity(HttpStatus.CREATED);
    }
}