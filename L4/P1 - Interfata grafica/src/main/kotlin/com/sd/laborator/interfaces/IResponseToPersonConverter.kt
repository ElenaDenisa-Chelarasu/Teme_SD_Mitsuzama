package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Person
import org.springframework.http.ResponseEntity

interface IResponseToPersonConverter {
    fun convertResponse(response: ResponseEntity<List<Map<String, Any>>>): List<Person>
}