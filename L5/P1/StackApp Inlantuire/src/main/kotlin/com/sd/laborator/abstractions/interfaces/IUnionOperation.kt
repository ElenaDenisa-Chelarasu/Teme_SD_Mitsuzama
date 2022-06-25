package com.sd.laborator.abstractions.interfaces

interface IUnionOperation {
    fun executeOperation(A: Set<Pair<Int, Int>>, B: Set<Pair<Int, Int>>): Set<Pair<Int, Int>>
}