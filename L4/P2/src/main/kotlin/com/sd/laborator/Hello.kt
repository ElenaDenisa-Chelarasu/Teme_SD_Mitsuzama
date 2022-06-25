package com.sd.laborator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
@SpringBootApplication
open class PasswordEncryption

fun main(args: Array<String>) {
    runApplication<PasswordEncryption>(*args)
}