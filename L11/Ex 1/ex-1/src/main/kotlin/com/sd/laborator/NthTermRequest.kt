package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class NthTermRequest {
    private lateinit var n: Integer

    fun getN(): Int = n.toInt()
}