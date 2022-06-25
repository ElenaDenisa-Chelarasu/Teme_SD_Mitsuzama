package com.sd.laborator.interfaces

interface ClientValidatorInterface {
    fun validateClient():Boolean
    fun getMessageForInvalidClient():String
}