package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class PressButtonRequest {
    private var button: String? = null
    fun getButton()=button
    fun setButton(button: String?) {
        this.button = button
    }
}