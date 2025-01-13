package fp.functional_programming_in_kotin.ch3


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
    val p1 = partial1(1) { a: Int, b: Int -> a + b }
    println(p1(2)) // = 3

    val curry = curry { a: Int, b: Int -> a + b }
    println(curry(1)(2)) // = 3

    val uncurry = uncurry { a: Int -> { b: Int -> a + b } }
    println(uncurry(1, 2)) // = 3

    val compose = compose({ b: Int -> b * 2 }, { a: Int -> a * 2 })
    println(compose(2)) // = 8

}
