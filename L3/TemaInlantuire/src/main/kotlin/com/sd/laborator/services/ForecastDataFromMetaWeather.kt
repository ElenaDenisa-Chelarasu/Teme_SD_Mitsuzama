package com.sd.laborator.services

import com.sd.laborator.interfaces.ForecastDataProvider
import com.sd.laborator.interfaces.TimeProviderInterface
import org.json.JSONObject
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.net.URL

@Service
class ForecastDataFromMetaWeather(private val timeService: TimeProviderInterface):ForecastDataProvider {
    override fun getForecastData(locationId: Int): JSONObject {
        // ID-ul locaţiei nu trebuie codificat, deoarece este numeric
        val forecastDataURL =
            URL("https://www.metaweather.com/api/location/$locationId/")
        // preluare conţinut răspuns HTTP la o cerere GET către URL-ul de mai sus
        val rawResponse: String = forecastDataURL.readText()
        val responseRootObject = JSONObject(rawResponse)
        val title = responseRootObject.getString("title")
        // parsare obiect JSON primit
        val response = responseRootObject.getJSONArray("consolidated_weather").getJSONObject(0)
        response.put("title", title)
        response.put("current_time", timeService.getCurrentTime())
        return response
    }
}