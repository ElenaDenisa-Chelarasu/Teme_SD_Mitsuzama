package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext

fun main() {
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("KafkaIntegration")

    // initializarea contextului de streaming
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))

    val lines: JavaReceiverInputDStream<String> =
        streamingContext.socketTextStream("localhost", 9999)

    lines.foreachRDD { rdd->
        rdd.foreach {
            println(it)
        }
    }

    streamingContext.start()
    streamingContext.awaitTerminationOrTimeout(10_000)
}