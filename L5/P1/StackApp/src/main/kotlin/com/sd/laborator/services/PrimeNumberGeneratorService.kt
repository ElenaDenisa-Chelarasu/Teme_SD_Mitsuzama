package com.sd.laborator.services

import com.sd.laborator.interfaces.PrimeNumberGenerator
import org.springframework.stereotype.Service

@Service
class PrimeNumberGeneratorService: PrimeNumberGenerator {
    private val primeNumbersIn1To100: Set<Int> = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)

    override fun generatePrimeNumber(): Int {
        return primeNumbersIn1To100.elementAt((0 until primeNumbersIn1To100.count()).random())
    }

}