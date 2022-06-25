package com.sd.laborator

import org.apache.spark.sql.SparkSession

fun main(args: Array<String>) {
    val spark = SparkSession.builder()
        .appName("Java Spark SQL example")
        .config("spark.master", "local[4]")
        .orCreate
    // Crearea unui DataFrame pe baza unei tabele "Person" stocata intr-o baza de date MySQL
    //val url = "jdbc:mysql://yourIP:yourPort/databaseName?user=yourUsername&password=yourPassword&serverTimezone=UTC"
    val url = "jdbc:mysql://localhost:3306/sd_database?user=admin&password=adminpw&serverTimezone=UTC"
    val df = spark.sqlContext()
                            .read()
                            .format("jdbc")
                            .option("url", url)
                            .option("dbtable", "Person")
                            .load()

    // Afisarea schemei DataFrame-ului
    df.printSchema()

    // Numararea persoanelor dupa varsta
    val countsByAge = df.groupBy("age").count()
    countsByAge.show()

    // Salvarea countsByAge in src/main/resources/sql_output in format JSON
    countsByAge.write().format("json").save("src/main/resources/sql_output")
}