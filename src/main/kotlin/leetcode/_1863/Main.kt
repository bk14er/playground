package leetcode._1863

fun subsetXORSum(nums: IntArray): Int {

    fun dfs(result: Int, nums: IntArray): Int {
        if (nums.isEmpty()) return result

        val newXor = result xor nums[0] xor nums.reduce(Int::xor)

        println(nums.joinToString(","))

        return dfs(newXor, nums.copyOfRange(1, nums.size))
    }

    return dfs(0, nums)
}

fun main() {
    println(subsetXORSum(intArrayOf(1, 3)))
//    check(subsetXORSum(intArrayOf(1, 3)) == 6)
//
//    check(subsetXORSum(intArrayOf(5, 1, 6)) == 28)
//
//    check(subsetXORSum(intArrayOf(3, 4, 5, 6, 7, 8)) == 480)

}