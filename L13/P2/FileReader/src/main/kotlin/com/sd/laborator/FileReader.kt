package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext

fun main() {
    val cores = Runtime.getRuntime().availableProcessors()
    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[$cores]")
        .setAppName("Spark Example")
        .set("spark.task.maxFailures", "6")
    // initializarea contextului Spark
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))

    streamingContext.fileStream<KeyClass, ValueClass, InputFormatClass>(dataDirectory)
}