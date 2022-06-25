package com.sd.laborator.helpers

import com.sd.laborator.interfaces.IResponseToPersonConverter
import com.sd.laborator.pojo.Person
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ResponseToPersonConverter:IResponseToPersonConverter {
    override fun convertResponse(response: ResponseEntity<List<Map<String, Any>>>): List<Person> {
        val body = response.body
        var id: Int
        var lastName: String
        var firstName: String
        var telephone: String
        val persons = body?.map {
            id = it.values.toList()[0] as Int
            lastName = it.values.toList()[1] as String
            firstName = it.values.toList()[2] as String
            telephone = it.values.toList()[3] as String
            Person(id,lastName,firstName,telephone)
        }?:listOf()
        return persons
    }
}