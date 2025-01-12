package hyperskill.bullscows

fun main() = try {
    BullsCows().start()
} catch (e: Exception) {
    println(e.message)
}

class BullsCows {

    private var turn = 0
    private val digits = println("Input the length of the secret code:").run { readln().toInt() }
    private val symbols = println("Input the number of possible symbols in the code:").run { readln().toInt() }
    private val chars = List(symbols) { if (it < 10) '0' + it else 'a' + it - 10 }
    private val range = chars.last().let { if (it >= 'a') "0-9, a-$it" else "0-$it" }

    private val secret =
        run { if (digits > symbols) error("Error: generate a secret number with a length of $digits because there aren't enough unique digits. ") }
            .run { chars.shuffled().take(digits).joinToString("") }
            .also { displayEntryMessage() }

    inner class Grader(num: String) {
        var cows = 0
        var bulls = 0

        init {
            num.forEachIndexed { i, c ->
                if (c in secret) if (c == secret[i]) bulls++ else cows++
            }
        }


        fun ok() = (bulls == digits).also {
            if (it) {
                println("Congratulations! You guessed the secret code.")
            }
        }

        override fun toString() = listOfNotNull(toText(bulls, "bull"), toText(cows, "cow"))
            .run { if (isEmpty()) "None" else joinToString(" and ") }

        private fun toText(n: Int, s: String) = if (n < 1) null else if (n < 2) "$n $s" else "$n ${s}s"

    }

    private fun turn(): Boolean = Grader(println("Turn ${++turn}:").run { readln() })
        .run { println("Grade : $this").run { ok() } }

    fun start() = println("Okay, let's start a game!")
        .also {
            while (!turn()) {
                continue
            }
        }

    private fun displayEntryMessage() {
        println(buildString {
            append("The secret is prepared: ")
            append("*".repeat(digits))
            append(" ($range).")
        })
    }

}