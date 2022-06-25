package com.sd.laborator.services

import com.sd.laborator.interfaces.IHtmlWrapper
import org.springframework.stereotype.Service

@Service
class HtmlWrapper:IHtmlWrapper {

    override fun wrap(content: String, title: String):String {
        val start="<!DOCTYPE html><html><head><title>$title</title></head><body>"
        val end="</body></html>"
        return start+content+end
    }
}