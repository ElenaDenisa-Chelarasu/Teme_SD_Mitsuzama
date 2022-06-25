package com.sd.laborator.business.interfaces
import com.sd.laborator.business.models.MatchPasswordModel
import com.sd.laborator.business.models.MyCustomResponse

interface IAuthService {
    fun encodePassword(password: String, encodingId: String):
            MyCustomResponse<Any>
    fun matchPassword(matchModel: MatchPasswordModel):
            MyCustomResponse<Any>
}