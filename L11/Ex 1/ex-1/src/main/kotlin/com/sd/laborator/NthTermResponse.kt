package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class NthTermResponse {
    private var message: String? = null
    private var term: Int? = null

    fun getMessage(): String? = message
    fun getTerm(): Int? = term

    fun setMessage(message: String?) {
        this.message = message
    }

    fun setTerm(term: Int?){
        this.term = term
    }
}