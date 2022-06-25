package com.sd.laborator.config

import com.sd.laborator.abstractions.abstractClasses.ResponsabilityChainNode
import com.sd.laborator.model.StacksWrapper
import com.sd.laborator.business.ComputeNode
import com.sd.laborator.business.RegenerateANode
import com.sd.laborator.business.RegenerateBNode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OperationResponsabilityChainFactory {
    @Bean
    open fun getOperationResponsabilityChain():ResponsabilityChainNode{
        val stacks=StacksWrapper()
        val nodes=listOf(RegenerateANode(),RegenerateBNode(),ComputeNode())
        nodes.forEach { it.SetStacks(stacks) }
        nodes[0].SetNext(nodes[1])
        nodes[1].SetNext(nodes[2])
        return nodes[0]
    }
}