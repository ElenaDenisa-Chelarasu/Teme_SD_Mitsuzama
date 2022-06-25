package com.sd.laborator

import jakarta.inject.Singleton

@Singleton
class NthTermService {

        fun computeNthTerm(n: Int): Int {
        if (n == 0)
            return 1
        val prev = computeNthTerm(n-1)
        return prev + 2 * prev / n
    }
}