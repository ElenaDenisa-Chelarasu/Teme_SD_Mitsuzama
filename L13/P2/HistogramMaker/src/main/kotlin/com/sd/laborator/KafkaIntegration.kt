package com.sd.laborator

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.TaskContext
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka010.*
import scala.Tuple2

fun main() {
    // configurarea Kafka
    val kafkaParams = mutableMapOf<String, Any>(
        "bootstrap.servers" to "localhost:9092",
        "key.deserializer" to StringDeserializer::class.java,
        "value.deserializer" to StringDeserializer::class.java,
        "group.id" to "use_a_separate_group_id_for_each_stream",
        "auto.offset.reset" to "latest",
        "enable.auto.commit" to false
    )

    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("KafkaIntegration")

    // initializarea contextului de streaming
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
    
    val topics = listOf("topicA", "topicB")

    // crearea unui flux de date direct (DirectStream)
    val stream: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
        streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(topics, kafkaParams)
    )
    stream.mapToPair{record: ConsumerRecord<String, String> -> Tuple2(record.key(), record.value()) }

    // crearea unui RDD (set de date imutabil distribuit)
    val offsetRanges =
        arrayOf( /* topicul, partitia, offset-ul de inceput, offset-ul final */
            OffsetRange.create("test", 0, 0, 100),
            OffsetRange.create("test", 1, 0, 100)
        )
    val rdd: JavaRDD<ConsumerRecord<String, String>> = KafkaUtils.createRDD(
        streamingContext.sparkContext(),
        kafkaParams,
        offsetRanges,
        LocationStrategies.PreferConsistent()
    )

    // obtinerea offset-urilor
    stream.foreachRDD { rdd ->
        val offsetRanges = (rdd.rdd() as HasOffsetRanges).offsetRanges()
        rdd.foreachPartition { consumerRecords ->
            val o = offsetRanges.get(TaskContext.get().partitionId())
            println(
                o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset()
            )
        }
    }

    // stocarea offset-urilor
    stream.foreachRDD { rdd ->
      val offsetRanges = (rdd.rdd() as HasOffsetRanges).offsetRanges()
      (stream.inputDStream() as CanCommitOffsets).commitAsync(offsetRanges)
    }

    stream.mapToPair { Tuple2(it.key(), it.headers()) }
        .countByValue()
        .foreachRDD { rdd->
        rdd.foreachPartition {
            it.forEach {
                println(it._2)
            }
        }
    }

    streamingContext.start()
    streamingContext.awaitTerminationOrTimeout(1000)
}