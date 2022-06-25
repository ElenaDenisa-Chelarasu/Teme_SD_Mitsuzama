import javafx.collections.transformation.SortedList
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import scala.Tuple2

fun main() {
    // configurarea Kafka
    val kafkaParams = mutableMapOf<String, Any>(
        "bootstrap.servers" to "localhost:9092",
        "key.deserializer" to StringDeserializer::class.java,
        "value.deserializer" to StringDeserializer::class.java,
        "group.id" to "use_a_separate_group_id_for_each_stream",
        "auto.offset.reset" to "earliest",
        "enable.auto.commit" to false
    )

    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("KafkaIntegration")

    // initializarea contextului de streaming
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))

    val topics = listOf("lab13p3")

    // crearea unui flux de date direct (DirectStream)
    val stream: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
        streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(topics, kafkaParams)
    )

    val listOfPairs = streamingContext.sparkContext().broadcast(mutableListOf<Pair<String, Long>>())
    val wordToFrequencyDS = stream.map { it.value() }.countByValue()
    wordToFrequencyDS.persist(StorageLevel.MEMORY_ONLY())
    var leastFrequent: Pair<String, Long>
    wordToFrequencyDS.foreachRDD { rdd->
        rdd.foreach {
            if(listOfPairs.value.size<5)
                listOfPairs.value.add(Pair(it._1, it._2))
            else {
                leastFrequent = listOfPairs.value.minByOrNull { it.second }!!
                if(it._2 > leastFrequent.second) {
                    listOfPairs.value.remove(leastFrequent)
                    listOfPairs.value.add(Pair(it._1, it._2))
                }
            }
        }
    }
    streamingContext.start()
    streamingContext.awaitTerminationOrTimeout(10_000)
    listOfPairs.value.forEach { println("${it.first} -> ${it.second}") }
}