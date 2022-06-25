package com.sd.laborator
import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneRequest {
    private lateinit var number: Integer
    fun getNumber(): Int {
        return number.toInt()
    }
}