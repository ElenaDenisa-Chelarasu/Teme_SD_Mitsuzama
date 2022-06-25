package com.sd.laborator
import io.micronaut.core.annotation.Introspected

@Introspected
class FilterPrimesResponse {
    private var message: String? = null
    private var primes: List<Int>? = null
    fun getPrimes(): List<Int>? {
        return primes
    }
    fun setPrimes(primes: List<Int>?) {
        this.primes = primes
    }
    fun getMessage(): String? {
        return message
    }
    fun setMessage(message: String?) {
        this.message = message
    }
}