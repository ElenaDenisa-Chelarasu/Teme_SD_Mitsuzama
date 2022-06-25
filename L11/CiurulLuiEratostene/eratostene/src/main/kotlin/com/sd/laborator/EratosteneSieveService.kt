package com.sd.laborator
import java.util.*
import jakarta.inject.Singleton

@Singleton
class EratosteneSieveService {
    // implementare preluata de la https://www.geeksforgeeks.org/sieve-eratosthenes-0n-time-complexity/
    val MAX_SIZE = 1000001
    // isPrime[] : isPrime[i] este adevarat daca numarul i este prim
    // prime[] : stocheaza toate numerele prime mai mici ca N
    // SPF[] (Smallest Prime Factor) - stocheaza cel mai mic factor prim
    // al numarului
    // [de exemplu : cel mai mic factor prim al numerelor '8' si '16'
    // este '2', si deci SPF[8] = 2 , SPF[16] = 2 ]
    private val isPrime = Vector<Boolean>(MAX_SIZE)
    private val SPF = Vector<Int>(MAX_SIZE)
    fun findPrimesLessThan(n: Int): List<Int> {
        val prime: MutableList<Int> = ArrayList()
        for (i in 2 until n) {
            if (isPrime[i]) {
                prime.add(i)
                // un numar prim este propriul sau cel mai mic factor prim
                SPF[i] = i
            }
            // Se sterg toti multiplii lui i * prime[j], care nu sunt
            // primi
            // setand isPrime[i * prime[j]] = false
            // si punand cel mai mic factor prim al lui i * prime[j] ca
            // si prime[j]
            // [de exemplu: fie i = 5, j = 0, prime[j] = 2 [i * prime[j]
            // = 10],
            // si deci cel mai mic factor prim al lui '10' este '2' care
            // este prime[j] ]
            // Aceasta bucla se executa doar o singura data pentru
            // numerele care nu sunt prime
            var j = 0
            while (j < prime.size && i * prime[j] < n && prime[j] <=
                SPF[i]) {
                isPrime[i * prime[j]] = false
                // se pune cel mai mic factor prim al lui i * prime[j]
                SPF[i * prime[j]] = prime[j]
                j++
            }
        }
        return prime
    }

    init {
        // initializare vectori isPrime si SPF
        for (i in 0 until MAX_SIZE) {
            isPrime.add(true)
            SPF.add(2)
        }
        // 0 and 1 are not prime
        isPrime[0] = false
        isPrime[1] = false
    }
}