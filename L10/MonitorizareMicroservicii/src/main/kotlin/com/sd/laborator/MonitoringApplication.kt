package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.trace
import kotlin.math.PI
import kotlin.math.sin

@SpringBootApplication
@EnableScheduling
open class MonitoringApplication

fun main(args: Array<String>) {
    val x = (0..100).map { it.toDouble() / 100.0 }
    val y = x.map { sin(2.0 * PI * it) }

    val plot = Plotly.plot {
        trace(x, y) {
            name = "for a single trace in graph its name would be hidden"
        }

        layout {
            title = "Graph name"
            xaxis {
                title = "x axis"
            }
            yaxis {
                title = "y axis"
            }
        }
    }
    runApplication<MonitoringApplication>(*args)
}
