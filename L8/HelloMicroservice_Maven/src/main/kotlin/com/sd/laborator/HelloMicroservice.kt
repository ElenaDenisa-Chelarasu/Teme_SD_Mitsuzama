package com.sd.laborator

import java.net.ServerSocket

fun main(args: Array<String>) {
    // se porneste un socket server TCP pe portul 2000 care asculta pentru conexiuni
    val server = ServerSocket(2000)
    println("Microserviciul se executa pe portul: ${server.localPort}")
    println("Se asteapta conexiuni...")

    while (true) {
        // se asteapta conexiuni din partea clientilor
        val client = server.accept()
        println("Client conectat: ${client.inetAddress.hostAddress}:${client.port}")

        // acest microserviciu simplu raspunde printr-un mesaj oricarui client se conecteaza
        client.getOutputStream().write("Hello from a dockerized microservice!\n".toByteArray())

        // dupa ce mesajul este trimis, se inchide conexiunea cu clientul
        client.close()
    }
}
