package com.sd.laborator.controllers
import com.fasterxml.jackson.databind.ObjectMapper

import com.github.fge.jsonpatch.JsonPatch
import com.sd.laborator.interfaces.IAgendaService
import com.sd.laborator.pojo.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AgendaController {
    @Autowired
    private lateinit var _agendaService: IAgendaService

    @RequestMapping(value = ["/person"], method = [RequestMethod.POST])
    fun createPerson(@RequestBody person: Person): ResponseEntity<Unit> {
        _agendaService.createPerson(person)
        return ResponseEntity(Unit, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getPerson(@PathVariable id: Int): ResponseEntity<Person?> {
        val person: Person? = _agendaService.getPerson(id)
        val status = if (person == null) {
            HttpStatus.NOT_FOUND
        } else {
            HttpStatus.OK
        }
        return ResponseEntity(person, status)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@PathVariable id: Int, @RequestBody person: Person)
    : ResponseEntity<Unit> {
        _agendaService.getPerson(id)?.let {
            _agendaService.updatePerson(it.id, person)
            return ResponseEntity(Unit, HttpStatus.ACCEPTED)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PATCH])
    fun patchPerson(@PathVariable id: Int, @RequestBody patchOperations: JsonPatch)
    : ResponseEntity<Unit> {
        _agendaService.getPerson(id)?.let {
    // aplicam operatiile de Patch peste obiectul gasit
            val objectMapper = ObjectMapper()
            val patchedPersonJsonNode =
                patchOperations.apply(objectMapper.valueToTree(it))
            val patchedPerson =
                objectMapper.treeToValue(patchedPersonJsonNode, Person::class.java)
    // updatam obiectul obtinut dupa operatia de patch
            _agendaService.updatePerson(it.id, patchedPerson)
            return ResponseEntity(Unit, HttpStatus.NO_CONTENT)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.DELETE])
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Unit> {
        if (_agendaService.getPerson(id) != null) {
            _agendaService.deletePerson(id)
            return ResponseEntity(Unit, HttpStatus.OK)
        } else {
            return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/agenda"], method = [RequestMethod.GET])
    fun search(
        @RequestParam(required = false, name = "lastName",
            defaultValue = "") lastName: String,
        @RequestParam(required = false, name = "firstName",
            defaultValue = "") firstName: String,
        @RequestParam(required = false, name =
            "telephone", defaultValue = "") telephoneNumber: String)
    : ResponseEntity<List<Person>> {
        val personList = _agendaService.searchAgenda(lastName,
            firstName, telephoneNumber)
        var httpStatus = HttpStatus.OK
        if (personList.isEmpty()) {
            httpStatus = HttpStatus.NO_CONTENT
        }
        return ResponseEntity(personList, httpStatus)
    }
}