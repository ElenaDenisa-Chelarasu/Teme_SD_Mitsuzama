package com.sd.laborator.business

import com.sd.laborator.abstractions.abstractClasses.ResponsabilityChainNode

class RegenerateBNode: ResponsabilityChainNode("regenerate_B") {
    override fun process(): String {
        stacks.B=generateStack(20)
        return "B~" + stacks.B?.data.toString()
    }
}