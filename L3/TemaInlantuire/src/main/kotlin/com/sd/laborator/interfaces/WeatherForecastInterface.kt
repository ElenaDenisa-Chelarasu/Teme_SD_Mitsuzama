package com.sd.laborator.interfaces
import com.sd.laborator.pojo.abstractions.AbstractForecastData

interface WeatherForecastInterface {
    fun getForecastData(locationId: Int): AbstractForecastData
}