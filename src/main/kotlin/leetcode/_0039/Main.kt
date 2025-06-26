package leetcode._0039

fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
    val result = ArrayList<List<Int>>()

    fun dfs(i: Int, current:List<Int>, total :Int){
        if(total == target){
            result.add(current.toList())
            return
        }

        val elementAt = candidates[i]
        dfs(i, current, total)
        dfs(i + 1, current, total)

    }


    dfs(0, listOf(), 0)

    return result
}


fun main() {
    check(combinationSum(intArrayOf(2, 3, 6, 7), 7) == listOf(listOf(2, 2, 3), listOf(7)))

    check(
        combinationSum(intArrayOf(2, 3, 5), 8) == listOf(
            listOf(2, 2, 2, 2),
            listOf(2, 3, 3),
            listOf(3, 5)
        )
    )

}