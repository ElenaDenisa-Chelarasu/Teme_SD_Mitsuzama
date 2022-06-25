package com.sd.laborator

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.sd.laborator")
		.start()
}

