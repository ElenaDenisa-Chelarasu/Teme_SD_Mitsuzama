package com.sd.laborator.business.models
data class MatchPasswordModel(
    var password: String = "",
    var hashPassword: String = "",
    var encodingId: String = "",
)