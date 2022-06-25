package com.sd.laborator

import io.micronaut.core.annotation.Introspected
@Introspected
class ProducerResponse {
    private var message: String? = null
    fun getMessage(): String? {
        return message
    }
    fun setMessage(message: String?) {
        this.message = message
    }
}