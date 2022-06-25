package com.sd.laborator
import io.micronaut.core.annotation.Introspected

@Introspected
class FilterPrimesRequest {
    private lateinit var numbers: List<Integer>
    fun getNumbers(): List<Int> = numbers.map { it.toInt() }
}