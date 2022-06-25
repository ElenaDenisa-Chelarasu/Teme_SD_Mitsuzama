package com.sd.laborator.business

import com.sd.laborator.abstractions.interfaces.ICartesianProductOperation

class CartesianProduct: ICartesianProductOperation {
    override fun executeOperation(A: Set<Int>, B: Set<Int>): Set<Pair<Int, Int>> {
        val result: MutableSet<Pair<Int, Int>> = mutableSetOf()
        A.forEach { a -> B.forEach { b -> result.add(Pair(a, b)) } }
        return result.toSet()
    }
}