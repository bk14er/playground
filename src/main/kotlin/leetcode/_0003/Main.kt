package leetcode._0003


fun lengthOfLongestSubstringV1(s: String): Int {
    if (s.isEmpty()) {
        return 0
    }
    if (s.isBlank()) {
        return 1
    }

    var slowerIndex = 0
    var fasterIndex = 0
    var maxLen = 0
    var currentMaxLen = 0
    val cache = hashSetOf<Char>()

    while (fasterIndex < s.length) {
        val current = s[fasterIndex]

        if (cache.contains(current)) {
            slowerIndex++
            fasterIndex = slowerIndex
            cache.clear()
            currentMaxLen = 0
        } else {
            currentMaxLen++
            cache.add(current)
            fasterIndex++
        }

        if (currentMaxLen > maxLen) {
            maxLen = currentMaxLen
        }

    }

    return maxLen
}

fun lengthOfLongestSubstring(s: String): Int {
    val lastSeen = mutableMapOf<Char, Int>()
    var maxLen = 0
    var start = 0

    for ((end, char) in s.withIndex()) {
        if (char in lastSeen && lastSeen[char]!! >= start) {
            start = lastSeen[char]!! + 1
        }
        lastSeen[char] = end
        maxLen = maxOf(maxLen, end - start + 1)
    }

    return maxLen
}


fun main() {
    check(lengthOfLongestSubstring("a") == 1)
    check(lengthOfLongestSubstring("") == 0)
    check(lengthOfLongestSubstring(" ") == 1)

    check(lengthOfLongestSubstring("abcabcbb") == 3)
    check(lengthOfLongestSubstring("bbbbb") == 1)
    check(lengthOfLongestSubstring("pwwkew") == 3)
}