package com.sd.laborator.business

import com.sd.laborator.abstractions.interfaces.IUnionOperation

class UnionOperation: IUnionOperation {
    override fun executeOperation(A: Set<Pair<Int, Int>>, B: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        return A union B
    }

}