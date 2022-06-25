package com.sd.laborator

import com.sd.laborator.generalMicroservices.CatalogMicroservice
import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {
    val catalogMicroservice = CatalogMicroservice()
    catalogMicroservice.run()
}