package com.sd.laborator.business

import com.sd.laborator.abstractions.abstractClasses.ResponsabilityChainNode

class RegenerateANode:ResponsabilityChainNode("regenerate_A") {
    override fun process(): String {
        stacks.A=generateStack(20)
        return "A~" + stacks.A?.data.toString()
    }
}