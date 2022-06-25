package com.sd.laborator.interfaces


interface IAgendaGuiService {
    fun getAgenda(): String
    fun getIndex(): String
    fun deletePerson(id: Int): String
    fun personForm(action: String, id: Int?): String
    fun updatePerson(id: Int, lastName: String, firstName: String, telephone: String): String
    fun createPerson(id: Int, lastName: String, firstName: String, telephone: String): String
}