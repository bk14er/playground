package leetcode

import kotlin.time.measureTime

object AlgoMetrics {

    fun measureTimeAndPrint(name: String = "", block: () -> Unit) {
        val time = measureTime {
            block()
        }

        println("Time $name: $time")
    }

}