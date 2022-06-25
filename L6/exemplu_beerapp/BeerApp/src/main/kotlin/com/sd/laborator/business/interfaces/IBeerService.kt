package com.sd.laborator.business.interfaces

import com.sd.laborator.models.Beer

interface IBeerService {
    fun createBeerTable()
    fun addBeer(beer: Beer)

    fun getBeers(): String
    fun getBeerByName(name: String): String?
    fun getBeerByPrice(price: Float): String

    fun updateBeer(beer: Beer)

    fun deleteBeer(name: String)
}