package com.sd.laborator
import com.sd.laborator.dbManagers.concretes.ButtonsDBManager
import com.sd.laborator.rabbitmq.concretes.RabbitMqMessageSender
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Controller
class Controller {
    @Get(uri="/", produces=["text/html"])
    fun index()= File("../src/main/resources/index.html").readText()

    @Post(uri="/")
    fun pressButton(@Body pressButtonRequest: PressButtonRequest) {
        handler.accept(pressButtonRequest)
    }

    @Get(uri="/clicks", produces=["application/json"])
    fun clicks(): String {
        val dbManager = ButtonsDBManager("../../../database.db")
        val map = dbManager.getButtonNamesAndFrequencies()
        return Json.encodeToString(map)
    }

    companion object {
        private val handler = PressButtonFunction()
    }
}