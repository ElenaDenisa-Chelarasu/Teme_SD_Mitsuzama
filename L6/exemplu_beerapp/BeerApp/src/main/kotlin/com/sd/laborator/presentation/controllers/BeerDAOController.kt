package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IBeerService
import com.sd.laborator.models.Beer
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BeerDAOController {
    @Autowired
    private lateinit var _beerService: IBeerService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    // citesc din queue1
    // scriu in queue
    @RabbitListener(queues = ["\${beerapp.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        val (operation, parameters) = msg.split('~')
        var beer: Beer? = null
        var price: Float? = null
        var name: String? = null

        // id=1;name=Corona;price=3.6
        if("id=" in parameters) {
            println(parameters)
            val params: List<String> = parameters.split(';')
            try {
                beer = Beer(
                    params[0].split('=')[1].toInt(),
                    params[1].split('=')[1],
                    params[2].split('=')[1].toFloat()
                )
            } catch (e: Exception) {
                print("Error parsing the parameters: ")
                println(params)
                return
            }
        } else if ("price=" in parameters) {
            price = parameters.split('=')[1].toFloat()
        } else if ("name=" in parameters) {
            name = parameters.split("=")[1]
        }
        println("Parameters: $parameters")
        println("Name: $name")
        println("Price: $price")
        println("Beer: $beer")
        val result: Any? = when(operation) {
            "createBeerTable" -> _beerService.createBeerTable()
            "addBeer" -> _beerService.addBeer(beer!!)
            "getBeers" -> _beerService.getBeers()
            "getBeerByName" -> _beerService.getBeerByName(name!!)
            "getBeerByPrice" -> _beerService.getBeerByPrice(price!!)
            "updateBeer" -> _beerService.updateBeer(beer!!)
            "deleteBeer" -> _beerService.deleteBeer(name!!)
            else -> null
        }
        println("Result: $result")
        if (result != null) sendMessage(result.toString())
    }

    private fun sendMessage(msg: String) {
        println("Message to send: $msg")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKey(), msg)
    }
}