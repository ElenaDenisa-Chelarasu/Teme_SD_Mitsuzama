package com.sd.laborator.services
import com.sd.laborator.interfaces.ForecastDataProvider
import com.sd.laborator.interfaces.JSONToPOJOForecastDataInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.abstractions.AbstractForecastData
import org.springframework.stereotype.Service

@Service
class WeatherForecastService (private val forecastDataProvider: ForecastDataProvider, private val jsonToPOJO: JSONToPOJOForecastDataInterface) :
    WeatherForecastInterface {
    override fun getForecastData(locationId: Int): AbstractForecastData {
        val forecastDataJSONObject = forecastDataProvider.getForecastData(locationId)
        return jsonToPOJO.getForecastDataPOJO(forecastDataJSONObject)
    }
}