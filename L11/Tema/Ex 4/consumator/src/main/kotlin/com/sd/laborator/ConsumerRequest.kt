package com.sd.laborator

import io.micronaut.core.annotation.Introspected
@Introspected
class ConsumerRequest {
    private var xmlData: String? = null
    fun getXmlData(): String? {
        return xmlData
    }
    fun setXmlData(message: String?) {
        this.xmlData = message
    }
}