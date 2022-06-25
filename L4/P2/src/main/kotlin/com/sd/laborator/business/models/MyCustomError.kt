package com.sd.laborator.business.models
import java.text.SimpleDateFormat
import java.util.*

data class MyCustomError(
    var status: Int,
    var error: String?,
    var message: String?,
    var timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()))