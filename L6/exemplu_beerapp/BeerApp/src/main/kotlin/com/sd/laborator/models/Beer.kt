package com.sd.laborator.models

class Beer(private var id: Int, private var name: String, private var price: Float) {

    var beerID: Int
        get() {
            return id
        }
        set(value) {
            id = value
        }
    var beerName: String
        get() {
            return name
        }
        set(value) {
            name = value
        }
    var beerPrice: Float
        get() {
            return price
        }
        set(value) {
            price = value
        }

    override fun toString(): String {
        return "Beer [id=$beerID, name=$beerName, price=$beerPrice]"
    }

}