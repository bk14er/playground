package leetcode._0017

import java.util.LinkedList


val keyboard = mapOf(
    '1' to "",
    '2' to "abc",
    '3' to "def",
    '4' to "ghi",
    '5' to "jkl",
    '6' to "mno",
    '7' to "pqrs",
    '8' to "tuv",
    '9' to "wxyz"
)

/**
 * Time complexity: O(n * 4^n)
 */
fun letterCombinationsBfs(digits: String): List<String> {
    if (digits.isEmpty()) {
        return emptyList()
    }
    val result = mutableListOf<String>()

    fun backtracking(i: Int, currentString: StringBuilder) {
        if (currentString.length == digits.length) {
            result.add(currentString.toString())
            return
        }

        val currentDigits = keyboard[digits[i]]!!

        for (segment in currentDigits) {
            backtracking(i + 1, currentString.append(segment))
            currentString.deleteCharAt(currentString.length - 1)
        }

    }

    backtracking(0, StringBuilder())


    return result
}

fun letterCombinationsBitmask(digits: String): List<String> {
    if (digits.isEmpty()) {
        return emptyList()
    }
    val combinations = mutableListOf<String>()

    val selectedKeys = digits.map { keyboard[it]!! }
    val allDigitCombinations = selectedKeys
        .map { it.length }.fold(1) { acc, i ->
            acc * i
        }

    for (bitmask in 0..<allDigitCombinations) {
        var current = bitmask
        val combination = mutableListOf<String>()

        for (digit in selectedKeys) {
            val length = digit.length
            val index = current % length
            combination.add(digit[index].toString())
            current /= length
        }
        combinations.add(combination.joinToString(""))
    }

    return combinations
}

fun letterCombinationsQueue(digits: String): List<String> {
    if (digits.isEmpty()) {
        return emptyList()
    }
    val combinations = LinkedList<String>()
    combinations.add("")

    for (i in digits.indices) {
        val x = digits[i]

        while (combinations.peek().length == i) {
            val t = combinations.remove()

            for (s in keyboard[x]!!.toCharArray()) {
                combinations.add(t + s)
            }

        }

    }

    return combinations
}


fun main() {
    check(
        letterCombinationsQueue("23").containsAll(
            listOf(
                "ad",
                "ae",
                "af",
                "bd",
                "be",
                "bf",
                "cd",
                "ce",
                "cf"
            )
        )
    )
//    check(letterCombinationsBitmask("23").containsAll(listOf("ad","ae","af","bd","be","bf","cd","ce","cf")))

//    check(letterCombinationsBfs("") == emptyList<String>())
//    check(letterCombinationsBfs("2") == listOf("a","b","c"))
//    check(letterCombinationsBfs("23") == listOf("ad","ae","af","bd","be","bf","cd","ce","cf"))
}