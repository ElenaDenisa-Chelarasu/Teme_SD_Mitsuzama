package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Person

interface IPersonToHtml {
    fun toHtml(person: Person?): String
    fun toHtml(list: List<Person>?):String
}