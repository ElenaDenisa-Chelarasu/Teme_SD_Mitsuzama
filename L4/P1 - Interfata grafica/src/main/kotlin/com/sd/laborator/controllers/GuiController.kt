package com.sd.laborator.controllers

import com.sd.laborator.interfaces.IAgendaGuiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class GuiController {
    @Autowired
    private lateinit var agendaGuiService: IAgendaGuiService

    @GetMapping(value = ["/agenda"])
    @ResponseBody
    fun agenda() = agendaGuiService.getAgenda()

    @GetMapping(value=["/"])
    @ResponseBody
    fun index() = agendaGuiService.getIndex()

    @GetMapping(value=["/deletePerson/{id}"])
    @ResponseBody
    fun deletePerson(@PathVariable id: Int)=agendaGuiService.deletePerson(id)

    @GetMapping(value=["/updatePerson/{id}"])
    @ResponseBody
    fun updatePersonGet(@PathVariable id: Int)=agendaGuiService.personForm("/updatePerson/$id",id)

    @PostMapping(value=["/updatePerson/{id}"])
    @ResponseBody
    fun updatePersonPost(@PathVariable id: Int,
                        @RequestParam lastName: String,
                        @RequestParam firstName: String,
                        @RequestParam telephone: String)=agendaGuiService.updatePerson(id, lastName, firstName, telephone)

    @GetMapping(value=["/createPerson"])
    @ResponseBody
    fun createPersonGet()=agendaGuiService.personForm("/createPerson",null)

    @PostMapping(value=["/createPerson"])
    @ResponseBody
    fun createPersonPost(@RequestParam id: Int,
                         @RequestParam lastName: String,
                         @RequestParam firstName: String,
                         @RequestParam telephone: String)=agendaGuiService.createPerson(id, lastName, firstName, telephone)
}