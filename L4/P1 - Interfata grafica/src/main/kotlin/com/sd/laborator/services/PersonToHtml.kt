package com.sd.laborator.services

import com.sd.laborator.interfaces.IPersonToHtml
import com.sd.laborator.pojo.Person
import org.springframework.stereotype.Service

@Service
class PersonToHtml: IPersonToHtml {
    private val nameToField=mapOf(
        "ID" to Person::id,
        "Prenume" to Person::firstName,
        "Nume" to Person::lastName,
        "Nr. telefon" to Person::telephoneNumber)

    override fun toHtml(person: Person?): String {
        if(person == null)
            return "<p>Persoana inexistenta!</p>"
        else {
            val stringBuilder = StringBuilder("<ul>")
            nameToField.forEach { name, field ->  stringBuilder.append("<li>$name: ${field(person)}</li>")}
            stringBuilder.append("<li><a href=\"updatePerson/${person.id}\">Actualizeaza</a></li>")
            stringBuilder.append("<li><a href=\"deletePerson/${person.id}\">Sterge</a></li>")
            return stringBuilder.append("</ul>").toString()
        }
    }

    override fun toHtml(list: List<Person>?): String {
        if(list==null)
            return "<p>Agenda goala!</p>"
        else{
            val stringBuilder = StringBuilder("<ul>")
            list.forEach { stringBuilder.append("<li>${toHtml(it)}</li>")}
            return stringBuilder.append("</ul>").toString()
        }
    }
}