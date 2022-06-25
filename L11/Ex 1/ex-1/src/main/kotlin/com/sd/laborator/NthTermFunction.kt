package com.sd.laborator

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import jakarta.inject.Inject

@FunctionBean("nthTerm")
class NthTermFunction: FunctionInitializer(), Function<NthTermRequest, NthTermResponse> {
    @Inject
    private lateinit var nthTermService: NthTermService

    private val LOG: Logger = LoggerFactory.getLogger(NthTermFunction::class.java)

    override fun apply(request: NthTermRequest): NthTermResponse {
        val n = request.getN()
        val respose = NthTermResponse()
        if (n>0) {
            respose.setMessage("OK")
            respose.setTerm(nthTermService.computeNthTerm(n))
        }
        else
            respose.setMessage("Indice negativ!")
        return respose
    }

    fun main(args : Array<String>) {
        val function = NthTermFunction()
        function.run(args) {
                context -> function.apply(context.get(NthTermRequest::class.java))
        }
    }
}