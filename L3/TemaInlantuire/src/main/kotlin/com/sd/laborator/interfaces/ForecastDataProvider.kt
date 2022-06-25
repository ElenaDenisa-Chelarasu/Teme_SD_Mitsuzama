package com.sd.laborator.interfaces

import org.json.JSONObject

interface ForecastDataProvider {
    fun getForecastData(locationId: Int):JSONObject
}