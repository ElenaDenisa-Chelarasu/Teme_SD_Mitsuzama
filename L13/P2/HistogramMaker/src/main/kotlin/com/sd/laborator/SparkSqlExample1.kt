package com.sd.laborator

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.sql.*
import org.apache.spark.sql.functions.col
import java.io.Serializable


// clasa bean
class Person: Serializable {
    var name: String? = null
    var age = 0
}


fun main(args: Array<String>) {
    // configurarea si crearea sesiunii Spark SQL
    val sparkSession = SparkSession.builder()
        .appName("Java Spark SQL example")
        .config("spark.master", "local")
        .orCreate

    // initializarea unui DataFrame prin citirea unui json
    val df: Dataset<Row> = sparkSession.sqlContext().read().json("src/main/resources/people.json")

    // afisarea continutului din DataFrame la consola
    df.show()

    // Afisarea schemei DataFrame-ului intr-o forma arborescenta
    df.printSchema();

    // Selectarea coloanei nume si afisarea acesteia
    df.select("name").show();

    // Selectarea tuturor datelor si incrementarea varstei cu 1
    df.select(col("name"), col("age").plus(1)).show();

    // Selectarea persoanelor cu varsta > 21 ani
    df.filter(col("age").gt(21)).show();

    // Numararea persoanelor dupa varsta
    df.groupBy("age").count().show();

    // Inregistarea unui DataFrame ca un SQL View temporar
    df.createOrReplaceTempView("people")

    // Utilizarea unei interogari SQL pentru a selecta datele
    val sqlDF: Dataset<Row> = sparkSession.sql("SELECT * FROM people")
    sqlDF.show()

    // Inregistrarea unui DataFrame ca un SQL View global temporar
    df.createGlobalTempView("people");

    // Un SQL View global temporar este legat de o baza de date a sistemului: `global_temp`
    sparkSession.sql("SELECT * FROM global_temp.people").show();

    // Un view global temporar este vizibil intre sesiuni
    sparkSession.newSession().sql("SELECT * FROM global_temp.people").show();

    // Crearea seturilor de date (Dataset)
    val person0 = Person() // instanta de clasa Bean
    person0.name = "Mihai"
    person0.age = 20
    val person1 = Person() // instanta de clasa Bean
    person1.name = "Ana"
    person1.age = 19

    // Este creat un codificator (Encoder) pentru bean-ul Java
    val personEncoder: Encoder<Person> = Encoders.bean(Person::class.java)
    // Se creeaza Dataset-ul de persoane
    val javaBeanDS: Dataset<Person> = sparkSession.createDataset(listOf(person0, person1), personEncoder)
    javaBeanDS.show()

    // Codificatoarele (Encoders) pentru tipurile primitive sunt furnizate de clasa Encoders
    val integerEncoder = Encoders.INT()
    val primitiveDS: Dataset<Int> = sparkSession.createDataset(listOf(1, 2, 3), integerEncoder)
    val transformedDS = primitiveDS.map(
        MapFunction { value: Int -> value + 1 } as MapFunction<Int, Int>,
        integerEncoder
    )
    transformedDS.collect() // [2, 3, 4]
    transformedDS.show() // afisarea listei transformate

    // DataFrame-urile pot fi convertite intr-un Dataset
    val path = "src/main/resources/people.json"
    val peopleDS: Dataset<Person> = sparkSession
        .read()
        .schema(personEncoder.schema())
        .json(path)
        .`as`(personEncoder)
    peopleDS.show()

    // Interoperabilitatea cu RDD-uri
    // Crearea unui RDD de obiecte Person dintr-un fisier text
    val peopleRDD: JavaRDD<Person> = sparkSession.read()
        .textFile("src/main/resources/people.txt")
        .javaRDD()
        .map { line ->
            val parts: List<String> = line.split(",")
            val person = Person()
            person.name = parts[0]
            person.age = parts[1].trim { it <= ' ' }.toInt()
            person // return
        }
    // Aplicarea unei scheme pe un RDD de Java Bean-uri pentru a obtine DataFrame
    val peopleDF: Dataset<Row> = sparkSession.createDataFrame(peopleRDD, Person::class.java)
    // Inregistrarea DataFrame-ului ca un view temporar
    peopleDF.createOrReplaceTempView("people")

    // Selectarea persoanelor intre 13 si 19 ani cu o interogare SQL
    val teenagersDF: Dataset<Row> = sparkSession.sql("SELECT name FROM people WHERE age BETWEEN 13 AND 19")

    // Coloanele dintr-un Row din rezultat pot fi accesate dupa indexul coloanei
    val stringEncoder = Encoders.STRING()
    val teenagerNamesByIndexDF = teenagersDF.map(
        MapFunction { row: Row -> "Name: " + row.getString(0) } as MapFunction<Row, String>,
        stringEncoder
    )
    teenagerNamesByIndexDF.show()

    // sau dupa numele coloanei
    val teenagerNamesByFieldDF = teenagersDF.map(
        MapFunction { row: Row -> "Name: " + row.getAs("name") } ,
        stringEncoder
    )
    teenagerNamesByFieldDF.show()
}

