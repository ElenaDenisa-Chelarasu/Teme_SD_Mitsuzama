package com.sd.laborator.interfaces

import com.sd.laborator.pojo.abstractions.AbstractForecastData
import org.json.JSONObject

interface JSONToPOJOForecastDataInterface {
    fun getForecastDataPOJO(jsonObject: JSONObject): AbstractForecastData
}