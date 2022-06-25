package com.sd.laborator.business

import com.sd.laborator.abstractions.abstractClasses.ResponsabilityChainNode
import com.sd.laborator.abstractions.interfaces.ICartesianProductOperation
import com.sd.laborator.abstractions.interfaces.IUnionOperation

class ComputeNode: ResponsabilityChainNode("compute") {
    private val unionOperation: IUnionOperation = UnionOperation()
    private val cartesianProductOperation: ICartesianProductOperation = CartesianProduct()

    override fun process(): String {
        if (stacks.A == null || stacks.A!!.data.isEmpty())
            stacks.A = generateStack(20)
        if (stacks.B == null || stacks.B!!.data.isEmpty())
            stacks.B = generateStack(20)
        if (stacks.A!!.data.count() == stacks.B!!.data.count()) {
            // (A x B) U (B x B)
            val partialResult1 = cartesianProductOperation.executeOperation(stacks.A!!.data, stacks.B!!.data)
            val partialResult2 = cartesianProductOperation.executeOperation(stacks.B!!.data, stacks.B!!.data)
            val result = unionOperation.executeOperation(partialResult1, partialResult2)
            return "compute~" + "{\"A\": \"" + stacks.A?.data.toString() +"\", \"B\": \"" + stacks.B?.data.toString() + "\", \"result\": \"" + result.toString() + "\"}"
        }
        return "compute~" + "Error: A.count() != B.count()"
    }
}