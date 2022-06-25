package com.sd.laborator;
import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import jakarta.inject.Inject

@FunctionBean("eratostene")
class FilterPrimesFunction : FunctionInitializer(),
    Function<FilterPrimesRequest, FilterPrimesResponse> {
    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService
    private val LOG: Logger = LoggerFactory.getLogger(FilterPrimesFunction::class.java)
    override fun apply(msg : FilterPrimesRequest) : FilterPrimesResponse {
        // preluare numere din parametrul de intrare al functiei
        val numbers = msg.getNumbers()
        // obtinere maxim
        val max = numbers.maxOrNull()
        val response = FilterPrimesResponse()
        // se verifica daca maximul nu depaseste maximul
        if(max == null)
        {
            LOG.error("Lista vida!")
            response.setMessage("Lista vida!")
            return response
        }
        if (max >= eratosteneSieveService.MAX_SIZE) {
            LOG.error("Maxim prea mare! $max > maximul de ${eratosteneSieveService.MAX_SIZE}")
            response.setMessage("Se accepta doar numere mai mici ca " + eratosteneSieveService.MAX_SIZE)
            return response
        }
        LOG.info("Se calculeaza primele $max numere prime ...")
        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        val primes = eratosteneSieveService.findPrimesLessThan(max+1)
        response.setPrimes(numbers.intersect(primes.toSet()).toList())
        response.setMessage("Calcul efectuat cu succes!")
        LOG.info("Calcul incheiat!")
        return response
    }
}
/**
 * This main method allows running the function as a CLI application
using: echo '{}' | java -jar function.jar
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) {
    val function = FilterPrimesFunction()
    function.run(args) { context -> function.apply(context.get(FilterPrimesRequest::class.java)) }
}