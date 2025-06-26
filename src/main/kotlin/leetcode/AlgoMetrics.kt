package leetcode

import kotlin.time.measureTime

object AlgoMetrics {

    fun runTests(
        testType: String,
        testCases: List<Triple<String, IntArray, Int>>,
        testFunc: (IntArray) -> Int,
    ) {
        for ((name, nums, expected) in testCases) {
            measureTimeAndPrint("$testType: $name") {
                val result = runCatching { check(testFunc(nums) == expected) { "Test $name failed!" } }

                if(result.isFailure){
                    println("Error for $testCases ${result.exceptionOrNull()}")
                }
            }
        }
    }

    fun measureTimeAndPrint(name: String = "", block: () -> Unit) {
        val time = measureTime {
            block()
        }

        println("Time $name: $time")
    }

}