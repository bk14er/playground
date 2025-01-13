package fp.functional_programming_in_kotin.ch2


object Example1 {

    fun fib(i: Int): Int {
        tailrec fun loop(n: Int, a: Int, b: Int): Int =
            if (n < 1) a else loop(n - 1, b, a + b)

        return loop(i, 0, 1)
    }

    private fun abs(n: Int): Int =
        if (n < 0) -n
        else n

    private fun factorial(i: Int): Int {
        fun go(n: Int, acc: Int): Int =
            if (n <= 0) acc
            else go(n - 1, n * acc)
        return go(i, 1)
    }

    fun formatAbs(x: Int): String {
        return formatResult("absolute", x, Example1::abs)
    }

    fun formatFactorial(x: Int): String {
        return formatResult("factorial", x, Example1::factorial)
    }

    private fun formatResult(name: String, n: Int, f: (Int) -> Int): String {
        val msg = "The %s of %d is %d."
        return msg.format(name, n, f(n))
    }

}

object Example2 {
    fun <A> findFirst(ss: Array<A>, key: A): Int {
        tailrec fun loop(n: Int): Int =
            when {
                n >= ss.size -> -1
                ss[n] == key -> n
                else -> loop(n + 1)
            }

        return loop(0)
    }

    val <T> List<T>.tail: List<T>
        get() = drop(1)

    val <T> List<T>.head: T
        get() = first()

    fun <A> isSorted(aa: List<A>, order: (A, A) -> Boolean): Boolean =
        when {
            aa.size <= 1 -> true // A list with 0 or 1 elements is always sorted
            !order(aa.head, aa.tail.head) -> false // Check the order of the first two elements
            else -> isSorted(aa.tail, order) // Recursively check the rest of the list
        }
}

fun <A, B, C> partial1(a: A, f: (A, B) -> C): (B) -> C =
    { b -> f(a, b) }

/**
 * currying, which converts a function f
 * of two arguments into a function with one argument that partially applies f
 */
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C =
    { a -> { b -> f(a, b) } }

/**
 * uncurry, which reverses the transformation of curry
 */
fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C =
    { a, b -> f(a)(b) }

/**
 * function composition, which feeds the output of one function to the input of another function.
 * Again, the implementation of this function is wholly determined by its type signature.
 */
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C =
    { a -> f(g(a)) }

fun main(args: Array<String>) {
    val p1 = fp.functional_programming_in_kotin.ch3.partial1(1) { a: Int, b: Int -> a + b }
    println(p1(2)) // = 3

    val curry = fp.functional_programming_in_kotin.ch3.curry { a: Int, b: Int -> a + b }
    println(curry(1)(2)) // = 3

    val uncurry = fp.functional_programming_in_kotin.ch3.uncurry { a: Int -> { b: Int -> a + b } }
    println(uncurry(1, 2)) // = 3

    val compose =
        fp.functional_programming_in_kotin.ch3.compose({ b: Int -> b * 2 }, { a: Int -> a * 2 })
    println(compose(2)) // = 8

}
