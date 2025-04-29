package leetcode._0401

import leetcode.AlgoMetrics.measureTimeAndPrint
import kotlin.math.min


fun readBinaryWatchIterative(turnedOn: Int): List<String> {
    if (turnedOn == 0) {
        return listOf("0:00")
    }
    if (turnedOn > 8) {
        return emptyList()
    }

    val results = mutableListOf<String>()

    // hoursByBits[hOn] will hold all hour‐values in 0..11 that have exactly hOn LEDs on
    val hoursByBits = List(5) { mutableListOf<Int>() }  // we only ever need 0–4 bits for hours
    for (h in 0..11) {
        val bits = h.countOneBits()       // Kotlin stdlib extension
        if (bits <= 4) hoursByBits[bits].add(h)
    }

    // minutesByBits[mOn] will hold all minute‐values in 0..59 that have exactly mOn LEDs on
    val minutesByBits = List(7) { mutableListOf<Int>() }  // 0–6 bits for 6 LEDs
    for (m in 0..59) {
        val bits = m.countOneBits()
        if (bits <= 6) minutesByBits[bits].add(m)
    }

    for (hOn in 0..min(4, turnedOn)) {
        val mOn = turnedOn - hOn

        if (mOn > 6) continue

        // fetch your precomputed candidates
        val validHours = hoursByBits[hOn]
        val validMinutes = minutesByBits[mOn]

        // combine each hour/minute pair into a formatted time string
        for (h in validHours) {
            for (m in validMinutes) {
                val minuteStr = m.toString().padStart(2, '0')
                results += "$h:$minuteStr"
            }
        }


    }
    return results
}


fun readBinaryWatchBfs(turnedOn: Int): List<String> {
    val results = mutableListOf<String>()

    // Constants for LED weights
    val hourLEDWeights = listOf(8, 4, 2, 1)
    val minuteLEDWeights = listOf(32, 16, 8, 4, 2, 1)

    // Function to calculate LED weights
    fun calculateLedWeights(idx: Int): Pair<Int, Int> {
        val hourWeight = if (idx in hourLEDWeights.indices) hourLEDWeights[idx] else 0
        val minuteWeight = if (idx - 4 in minuteLEDWeights.indices) minuteLEDWeights[idx - 4] else 0
        return Pair(hourWeight, minuteWeight)
    }

    fun backtrack(idx: Int, countOn: Int, hourSum: Int, minSum: Int) {
        if (countOn > turnedOn || hourSum > 11 || minSum > 59) { // Adjusted limit check
            return
        }
        if (countOn == turnedOn) {
            results += "${hourSum}:${minSum.toString().padStart(2, '0')}"
            return
        }
        if (countOn + (10 - idx) < turnedOn) {
            return
        }

        // LED OFF
        backtrack(idx + 1, countOn, hourSum, minSum)

        // LED ON
        val (hourWeight, minuteWeight) = calculateLedWeights(idx)
        backtrack(idx + 1, countOn + 1, hourSum + hourWeight, minSum + minuteWeight)
    }

    backtrack(0, 0, 0, 0)
    return results
}

/**
 * 401. Binary Watch
 * https://leetcode.com/problems/binary-watch/description/?envType=problem-list-v2&envId=24vvbd7s
 */
fun main() {
    measureTimeAndPrint("BFS") { println(readBinaryWatchBfs(1)) }
    measureTimeAndPrint("Iterative") { println(readBinaryWatchIterative(1)) }
}
