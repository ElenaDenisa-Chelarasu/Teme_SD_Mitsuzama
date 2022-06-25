package com.sd.laborator.abstractions.abstractClasses

import com.sd.laborator.model.Stack
import com.sd.laborator.model.StacksWrapper
import org.springframework.stereotype.Service

@Service
abstract class ResponsabilityChainNode(private val task: String="") {
    private val primeGenerator: com.sd.laborator.abstractions.interfaces.IPrimeNumberGenerator = com.sd.laborator.business.PrimeNumberGenerator()
    protected var next: ResponsabilityChainNode?=null
    protected lateinit var stacks: StacksWrapper

    protected fun generateStack(count: Int):Stack?{
        if (count < 1)
            return null
        val X: MutableSet<Int> = mutableSetOf()
        while (X.count() < count)
            X.add(primeGenerator.generatePrimeNumber())
        return Stack(X)
    }

    fun doTask(task: String): String{
        if(task==this.task)
            return process()
        else return next?.doTask(task)?:"compute~Error: The task \"$task\" in invalid!"
    }

    fun SetStacks(stacks: StacksWrapper){
        this.stacks=stacks
    }

    fun SetNext(next:ResponsabilityChainNode){
        this.next=next
    }

    protected abstract fun process(): String
}