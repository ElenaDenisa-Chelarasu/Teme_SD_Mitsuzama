package com.sd.laborator.abstractions.interfaces

interface ICartesianProductOperation {
    fun executeOperation(A: Set<Int>, B: Set<Int>): Set<Pair<Int, Int>>
}