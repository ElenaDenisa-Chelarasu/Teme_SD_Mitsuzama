package com.sd.laborator.business.models
data class MyCustomResponse<T> (
    var successfulOperation: Boolean,
    var code: Int,
    var data: T,
    var error: String? = "",
    var message: String? = "",
)