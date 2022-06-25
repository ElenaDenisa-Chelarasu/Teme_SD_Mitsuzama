package com.sd.laborator.services

import com.sd.laborator.interfaces.JSONToPOJOForecastDataInterface
import com.sd.laborator.pojo.abstractions.AbstractForecastData
import com.sd.laborator.pojo.MetaweatherForecastData
import org.json.JSONObject
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class MetaweatherJSONToPOJO:JSONToPOJOForecastDataInterface {
    override fun getForecastDataPOJO(jsonObject: JSONObject): AbstractForecastData {
        // construire şi returnare obiect POJO care încapsulează datele meteo
        return MetaweatherForecastData(
            location = jsonObject.getString("title"),
            date = jsonObject.getString("current_time"),
            weatherState =
            jsonObject.getString("weather_state_name"),
            weatherStateIconURL =
            "https://www.metaweather.com/static/img/weather/png/${jsonObject.getString("weather_state_abbr")}.png",
            windDirection =
            jsonObject.getString("wind_direction_compass"),
            windSpeed =
            jsonObject.getFloat("wind_speed").roundToInt(),
            minTemp = jsonObject.getFloat("min_temp").roundToInt(),
            maxTemp = jsonObject.getFloat("max_temp").roundToInt(),
            currentTemp =
            jsonObject.getFloat("the_temp").roundToInt(),
            humidity = jsonObject.getFloat("humidity").roundToInt()
        )
    }

}