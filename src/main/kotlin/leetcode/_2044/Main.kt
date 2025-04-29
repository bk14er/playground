package leetcode._2044

import leetcode.AlgoMetrics.measureTimeAndPrint
import java.util.BitSet
import kotlin.math.min

fun countMaxOrSubsetsBruteForce(nums: IntArray): Int {
    val maxOr = nums.reduce { acc, i -> i or acc }

    fun dfs(i: Int, currentOr: Int): Int {

        if (i == nums.size) {
            if (maxOr == currentOr) {
                return 1
            }
            return 0
        }



        return dfs(i + 1, currentOr) + dfs(i + 1, currentOr or nums[i])
    }

    return dfs(0, 0)
}

fun countMaxOrSubsetsBruteForceMemo(nums: IntArray): Int {
    val maxOr = nums.reduce { acc, i -> i or acc }

    val cache = List(nums.size) {
        MutableList(maxOr + 1) {
            -1
        }
    }

    fun dfs(i: Int, currentOr: Int): Int {
        if (i == nums.size) {
            if (maxOr == currentOr) {
                return 1
            }
            return 0
        }

        if (cache[i][currentOr] != -1) {
            return cache[i][currentOr]
        }

        cache[i][currentOr] = (dfs(i + 1, currentOr) + dfs(i + 1, currentOr or nums[i]))

        return cache[i][currentOr]
    }

    return dfs(0, 0)
}

/**
 * Time complexity:  O(n * maxOr)
 * Memory :  O(maxOr)
 */
fun countMaxOrSubsetsBottomUp(nums: IntArray): Int {
    var maxOr = 0
    val dp = mutableMapOf(0 to 1)

    for (n in nums) {
        val temp = HashMap(dp)

        for ((currentOr, count) in dp) {
            val newOr = n or currentOr
            temp[newOr] = (temp[newOr] ?: 0) + count
        }

        dp.clear()
        dp.putAll(temp)

        maxOr = maxOr or n
    }

    return dp[maxOr]!!
}

/**
 * Time complexity:  O(n * 2^n)
 * Memory :  O(1)
 */
fun countMaxOrSubsetsBitMask(nums: IntArray): Int {
    val maxOr = nums.reduce { acc, i -> i or acc }

    val length = nums.size
    var res = 0
    val totalSubsets = 1 shl length
    for (subset in 1..totalSubsets) {
        var curOr = 0

        for(i in 0..<length){

            if( ((1 shl i) and subset) != 0 ){
                curOr = curOr or nums[i]
            }

        }

        if(curOr == maxOr){
            ++res
        }

    }

    return res
}


fun main() {
    val testCases = listOf(
        Triple("maxSubset_case1", intArrayOf(5000, 5001), 2),
        Triple("maxSubset_case1", intArrayOf(3, 1), 2),
        Triple("maxSubset_case2", intArrayOf(2, 2, 2), 7),
        Triple("maxSubset_case3", intArrayOf(3, 2, 1, 5), 6)
    )

    println("Running brute force tests:")
    runTests("Brute Force", testCases, ::countMaxOrSubsetsBruteForce)

    println("Running brute force with memoization tests:")
    runTests("Brute Force Memo", testCases, ::countMaxOrSubsetsBruteForceMemo)

    println("Running dynamic programming solution:")
    runTests("Bottom up", testCases, ::countMaxOrSubsetsBottomUp)

    println("Running bitmask solution:")
    runTests("Bitmask", testCases, ::countMaxOrSubsetsBitMask)

}

private fun runTests(
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