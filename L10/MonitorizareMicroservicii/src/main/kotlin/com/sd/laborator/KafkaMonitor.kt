package com.sd.laborator

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.PartitionOffset
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLEncoder
import java.time.LocalDateTime

@Component
class KafkaMonitor {
    companion object {
        private var numberOfBids: Int = 0
        private var numberOfProcessedBids: Int = 0

        private var BASE_DOCKER_API_COMMAND = "curl --unix-socket /var/run/docker.sock http:/v1.40"
    }

    init {
        println("KakfaMonitor instantiated!")
    }

    @KafkaListener(
        groupId = "KafkaMonitor",
        topicPartitions = [
            TopicPartition(
                topic = "topic_oferte",
                partitionOffsets = [
                    PartitionOffset(partition = "0", initialOffset =  "0"),
                    PartitionOffset(partition = "1", initialOffset =  "0"),
                    PartitionOffset(partition = "2", initialOffset =  "0"),
                    PartitionOffset(partition = "3", initialOffset =  "0")
                ]
            ),
            TopicPartition(
                topic = "topic_oferte_procesate",
                partitionOffsets = [
                    PartitionOffset(partition = "0", initialOffset =  "0")
                ]
            )
        ]
    )
    fun monitorKafkaMessages(message: ConsumerRecord<String, String>) {
        when(message.topic()) {
            "topic_oferte" -> ++numberOfBids
            "topic_oferte_procesate" -> ++numberOfProcessedBids
        }
    }

    @Scheduled(fixedDelay=1000)
    fun showKafkaStats() {
        println("[${LocalDateTime.now()}] Grad incarcare Auctioneer: $numberOfBids oferte primite")
        println("[${LocalDateTime.now()}] Grad incarcare MessageProcessor: $numberOfProcessedBids oferte procesate")
    }

    @Scheduled(fixedDelay=2000)
    fun showAuctioneerContainersStats() {
        // preluare lista de instante ale Auctioneer
        val auctioneerContainersFilter = URLEncoder.encode("{\"ancestor\": [\"auctioneer_docker_auctioneer:latest\"]}", "utf-8")
        val auctioneerListProcess: Process =
            Runtime.getRuntime().exec("$BASE_DOCKER_API_COMMAND/containers/json?filters=$auctioneerContainersFilter")
        val auctioneerListProcessInput = BufferedReader(InputStreamReader(auctioneerListProcess.inputStream))

        // se citeste raspunsul de la Docker Engine API sub forma de JSON
        val auctioneerListOutput = auctioneerListProcessInput.readLine()
        auctioneerListProcessInput.close()

        // se preia lista de ID-uri ale container-elor de tip Auctioneer
        val containerIdRegex = Regex("\"Id\":\"([a-f0-9]*)\"")
        containerIdRegex.findAll(auctioneerListOutput).forEach {
            // pentru fiecare ID in parte se preiau statisticile la runtime
            val auctioneerContainerID = it.groupValues[1].take(12)

            // se foloseste ruta /containers/ID/stats de la API-ul Docker Engine
            val auctioneerContainerStatsProcess: Process =
                Runtime.getRuntime().exec("$BASE_DOCKER_API_COMMAND/containers/$auctioneerContainerID/stats?stream=0")
            val auctioneerContainerStatsProcessInput = BufferedReader(InputStreamReader(auctioneerContainerStatsProcess.inputStream))

            // din output-ul cu statistici, se colecteaza utilizarea CPU si a memoriei
            val auctioneerContainerStatsOutput = auctioneerContainerStatsProcessInput.readLine()

            val cpuUsageRegex = Regex("\"cpu_stats\":\\{\"cpu_usage\":\\{\"total_usage\":([0-9]*),")
            cpuUsageRegex.find(auctioneerContainerStatsOutput)?.groupValues?.get(1).let {
                println("[$auctioneerContainerID] Utilizare procesor: $it")
            }

            val memoryUsageRegex = Regex("\"memory_stats\":\\{\"usage\":([0-9]*),")
            memoryUsageRegex.find(auctioneerContainerStatsOutput)?.groupValues?.get(1).let {
                println("[$auctioneerContainerID] Utilizare memorie: $it")
            }
        }
    }
}