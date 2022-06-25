package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IAuthService
import com.sd.laborator.business.models.MatchPasswordModel
import com.sd.laborator.presentation.utils.ControllerUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@RestController
class PasswordEncryptionController {
    @Autowired
    private lateinit var _authService: IAuthService
    @RequestMapping(value = ["/encrypt"], method = [RequestMethod.GET])
    fun encryptPassword(
        @RequestParam() password: String,
        @RequestParam() encodingId: String)
    : ResponseEntity<Any>
    {
        val response = _authService.encodePassword(password,
            encodingId)
        return ControllerUtils.makeResponse(response)
    }
    @RequestMapping(value = ["/match"], method = [RequestMethod.POST])
    fun matchPassword(@RequestBody matchModel: MatchPasswordModel):ResponseEntity<Any>
    {
        val response = _authService.matchPassword(matchModel)
        return ControllerUtils.makeResponse(response)
    }
}