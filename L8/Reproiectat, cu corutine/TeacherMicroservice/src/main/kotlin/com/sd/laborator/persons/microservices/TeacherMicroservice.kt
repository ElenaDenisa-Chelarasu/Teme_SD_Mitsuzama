package com.sd.laborator.persons.microservices

import com.sd.laborator.persons.abstractClasses.AbstractSchoolEmployee

class TeacherMicroservice : AbstractSchoolEmployee("TeacherMicroservice", TEACHER_PORT, TEACHER_ID) {
    companion object {
        const val TEACHER_PORT = 1600
        const val TEACHER_ID = 20
    }
}