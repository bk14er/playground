package leetcode._0022


fun generateParenthesisBackTrack(n: Int): List<String> {
    val result = mutableListOf<String>()


    fun backtracking(s: String, open: Int, close: Int) {
        if(s.length == n * 2){
            result.add(s)
            return
        }


        if (open < n) {
            backtracking(s + "(", open + 1, close)
        }
        if (close < open) {
            backtracking("$s)", open, close + 1)
        }


    }

    backtracking("",0,0)

    return result
}

fun main() {
    println(generateParenthesisBackTrack(1))
    println(generateParenthesisBackTrack(2))
    println(generateParenthesisBackTrack(3))
}