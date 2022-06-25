package com.sd.laborator.pojo

import com.sd.laborator.pojo.abstractions.AbstractForecastData

data class MetaweatherForecastData(override var location: String,
                                   override var date: String,
                                   override var weatherState: String,
                                   var weatherStateIconURL: String,
                                   var windDirection: String,
                                   var windSpeed: Int, // km/h
                                   var minTemp: Int, // grade celsius
                                   var maxTemp: Int,
                                   var currentTemp: Int,
                                   var humidity: Int): AbstractForecastData()