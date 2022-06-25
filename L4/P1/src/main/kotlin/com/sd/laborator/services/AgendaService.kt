package com.sd.laborator.services;
import com.sd.laborator.interfaces.IAgendaService
import com.sd.laborator.pojo.Person
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class AgendaService : IAgendaService {
    companion object {
        val initialAgenda = arrayOf(
            Person(1, "Hello", "Kotlin", "1234"),
            Person(2, "Hello", "Spring", "5678"),
            Person(3, "Hello", "Microservice", "9101112")
        )
    }
    private val agenda = ConcurrentHashMap<Int, Person>(
        initialAgenda.associateBy { person: Person -> person.id }
    )
    override fun getPerson(id: Int): Person? {
        return agenda[id]
    }
    override fun createPerson(person: Person) {
        agenda[person.id] = person
    }
    override fun deletePerson(id: Int) {
        agenda.remove(id)
    }
    override fun updatePerson(id: Int, person: Person) {
        deletePerson(id)
        createPerson(person)
    }
    override fun searchAgenda(lastNameFilter: String,
                              firstNameFilter: String,
                              telephoneNumberFilter: String)
    : List<Person> {
        return agenda.filter {
            it.value.lastName.toLowerCase().contains(lastNameFilter,
                ignoreCase = true) &&
                    it.value.firstName.toLowerCase().contains(firstNameFilter, ignoreCase
                    = true) &&
                    it.value.telephoneNumber.contains(telephoneNumberFilter)
        }.map {
            it.value
        }.toList()
    }
}