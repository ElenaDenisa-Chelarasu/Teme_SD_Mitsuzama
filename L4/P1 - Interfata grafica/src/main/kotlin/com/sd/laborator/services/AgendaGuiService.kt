package com.sd.laborator.services

import com.sd.laborator.helpers.RestTemplateFactory
import com.sd.laborator.interfaces.IAgendaGuiService
import com.sd.laborator.interfaces.IHtmlWrapper
import com.sd.laborator.interfaces.IPersonToHtml
import com.sd.laborator.interfaces.IResponseToPersonConverter
import com.sd.laborator.pojo.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.getForEntity


@Service
class AgendaGuiService: IAgendaGuiService {
    @Autowired
    private lateinit var htmlWrapper: IHtmlWrapper

    @Autowired
    private lateinit var personToHtml: IPersonToHtml

    @Autowired
    private lateinit var responseConverter: IResponseToPersonConverter

    private val acasa = "<br/><a href=\"/\">Acasa</a>"

    private fun getPersons(): List<Person> {
        val url="https://localhost:8080/agenda"
        val restTemplate = RestTemplateFactory.getRestTemplate(RestTemplateFactory.Companion.RestTemplateTypes.WithoutSslVerification)
        val response = restTemplate.getForEntity<List<Map<String, Any>>>(url)
        return responseConverter.convertResponse(response)
    }

    override fun getAgenda(): String {
        val persoane = getPersons()
        if(persoane.size>0){
            val persoaneHtmlList = personToHtml.toHtml(persoane)
            return htmlWrapper.wrap(persoaneHtmlList, "Agenda")
        }else{
            return htmlWrapper.wrap("Agenda goala", "Agenda")
        }
    }

    override fun getIndex(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<a href=\"/agenda\">Agenda</a><br/>")
        stringBuilder.append("<a href=\"/createPerson\">Adaugare persoana</a>")
        return htmlWrapper.wrap(stringBuilder.toString(), "Acasa")
    }

    override fun deletePerson(id: Int): String {
        try {
            val url="https://localhost:8080/person/$id"
            val restTemplate = RestTemplateFactory.getRestTemplate(RestTemplateFactory.Companion.RestTemplateTypes.WithoutSslVerification)
            restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity(""), String.Companion::class.java)
            return htmlWrapper.wrap("Persoana a fost eliminata cu succes!$acasa", "Succes!")
        }catch(ex: Exception){
            return htmlWrapper.wrap("Nici o persoana nu are ID-ul $id.$acasa","Eroare!")
        }
    }

    override fun personForm(action: String, id: Int?): String {
        val person:Person?
        val readonlyId: String
        if(id!=null)
        {
            person = getPersons().find { it.id==id }
            if(person==null)
                return htmlWrapper.wrap("Nici o persoana nu are ID-ul $id!$acasa", "Eroare!")
            readonlyId="readonly"
        }
        else {
            person = Person()
            readonlyId=""
        }
        val stringBuilder = StringBuilder("<form action=\"$action\" method=\"POST\">")
        stringBuilder.append("ID: <input type=\"number\" name=\"id\" value=\"${person.id}\" $readonlyId required><br/>")
        stringBuilder.append("Nume: <input type=\"text\" name=\"lastName\" value=\"${person.lastName}\" required><br/>")
        stringBuilder.append("Prenume: <input type=\"text\" name=\"firstName\" value=\"${person.firstName}\" required><br/>")
        stringBuilder.append("Telefon: <input type=\"text\" name=\"telephone\" value=\"${person.telephoneNumber}\" required><br/>")
        stringBuilder.append("<input type=\"submit\" value=\"Trimite\"></form>")
        return htmlWrapper.wrap(stringBuilder.append(acasa).toString(), "Formular")
    }

    override fun updatePerson(id: Int, lastName: String, firstName: String, telephone: String): String{
        try{
            val person=Person(id, lastName, firstName, telephone)
            val url="https://localhost:8080/person/$id"
            val restTemplate = RestTemplateFactory.getRestTemplate(RestTemplateFactory.Companion.RestTemplateTypes.WithoutSslVerification)
            restTemplate.exchange(url, HttpMethod.PUT, HttpEntity(person), String.Companion::class.java)
            return htmlWrapper.wrap("Persoana a fost actualizata cu succes!$acasa", "Succes!")
        }catch(ex: Exception){
            return htmlWrapper.wrap("Nici o persoana nu are ID-ul $id.$acasa","Eroare!")
        }
    }

    override fun createPerson(id: Int, lastName: String, firstName: String, telephone: String): String{
        if(getPersons().count{it.id==id}>0)
            return htmlWrapper.wrap("Exista deja o persoana cu ID de valoare $id!$acasa", "Eroare!")
        val person=Person(id,lastName,firstName,telephone)
        val url="https://localhost:8080/person"
        val restTemplate = RestTemplateFactory.getRestTemplate(RestTemplateFactory.Companion.RestTemplateTypes.WithoutSslVerification)
        restTemplate.exchange(url, HttpMethod.POST, HttpEntity(person), String.Companion::class.java)
        return htmlWrapper.wrap("Persoana adaugata cu succes!$acasa", "Succes!")
    }
}