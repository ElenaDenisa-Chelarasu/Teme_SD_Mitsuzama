package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function
import org.apache.spark.api.java.function.Function2
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.storage.StorageLevel


internal class GetLength : Function<String?, Int?> {
    override fun call(p0: String?): Int? {
        return p0?.length ?: 0
    }
}

internal class Sum : Function2<Int?, Int?, Int?> {
    override fun call(p0: Int?, p1: Int?): Int? {
        return (p0?.toInt() ?: 0) + (p1?.toInt() ?: 0)
    }
}


fun main(args: Array<String>) {
    val cores = Runtime.getRuntime().availableProcessors()
    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[$cores]")
        .setAppName("Spark Example")
        .set("spark.task.maxFailures", "6")
    // initializarea contextului Spark
    val sparkContext = JavaSparkContext(sparkConf)

    val items = listOf("123/643/7563/2134/ALPHA", "2343/6356/BETA/2342/12", "23423/656/343")
    // paralelizarea colectiilor
    val distributedDataset = sparkContext.parallelize(items)

    // 1) spargerea fiecarui string din lista intr-o lista de substring-uri si reunirea intr-o singura lista
    // 2) filtrarea cu regex pentru a pastra doar numerele
    // 3) conversia string-urilor filtrate la int prin functia de mapare
    // 4) sumarea tuturor numerelor prin functia de reducere
    val sumOfNumbers = distributedDataset.flatMap { it.split("/").iterator() }
        .filter { it.matches(Regex("[0-9]+")) }
        .map { it.toInt() }
        .reduce {total, next -> total + next }
    println(sumOfNumbers)

    // seturi de date externe
    // setul de date nu este inca incarcat in memorie (si nu se actioneaza inca asupra lui)
    val lines = sparkContext.textFile("src/main/resources/data.txt")

    // pentru utilizarea unui RDD de mai multe ori, trebuie apelata metoda persist:
    lines.persist(StorageLevel.MEMORY_ONLY())

    // functia de mapare reprezinta o transformare a setului de date initial (nu este calculat imediat)
    // abia cand se ajunge la functia de reducere (care este o actiune) Spark imparte operatiile in task-uri
    // pentru a fi rulate pe masini separate (fiecare masina executand o parte din map si reduce)
    // exemplu cu functii lambda:
    val totalLength0 = lines.map { s->s.length }.reduce { a: Int, b: Int -> a + b }

    // trimiterea unor functii catre Spark
    val totalLength1= lines.map(object : Function<String?, Int?> {
            override fun call(p0: String?): Int? {
                return p0?.length ?: 0
            }
        }).reduce(object : Function2<Int?, Int?, Int?> {
            override fun call(p0: Int?, p1: Int?): Int? {
                return (p0?.toInt() ?: 0) + (p1?.toInt() ?: 0)
            }
        })

    // sau daca scrierea functiilor inline sau a celor lambda este greoaie, se pot utiliza clase
    val totalLength2 = lines.map(GetLength()).reduce(Sum())
    println(totalLength0)
    println(totalLength1)
    println(totalLength2)

    // variabila partajata de tip broadcast
    // trimiterea unui set de date ca input catre fiecare nod intr-o maniera eficienta:
    val broadcastVar: Broadcast<List<Int>> = sparkContext.broadcast(listOf(1, 2, 3))
    val totalLength3 = lines.map { s->s.length + broadcastVar.value()[0] }.reduce { a: Int, b: Int -> a + b }
    println(totalLength3)

    //variabila partajata de tip acumulator
    val accumulator = sparkContext.sc().longAccumulator()
    sparkContext.parallelize(listOf(1, 2, 3, 4)).foreach { x -> accumulator.add(x.toLong()) }
    println(accumulator)

    // oprirea contextului Spark
    sparkContext.stop()
}