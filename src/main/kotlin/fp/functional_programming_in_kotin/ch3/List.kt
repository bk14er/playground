package fp.functional_programming_in_kotin.ch3

import fp.functional_programming_in_kotin.ch3.List.Companion.empty
import fp.functional_programming_in_kotin.ch3.List.Companion.foldLeft
import fp.functional_programming_in_kotin.ch3.List.Companion.foldRight

typealias Identity<B> = (B) -> B

// Sealed definition of the data type
sealed class List<out A> {

    companion object {

        fun <A> empty(): List<A> = Nil

        fun <A> of(vararg aa: A): List<A> {
            val tail = aa.sliceArray(1 until aa.size)
            return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
        }

        /**
         * Implement the function tail for removing the first element of a List.
         * Note that the function takes constant time.
         * What different choices can you make in your implementation
         * if the List is Nil? We’ll return to this question in the next chapter.
         */
        fun <A> tail(xs: List<A>): List<A> = when (xs) {
            is Nil -> error("List is empty")
            is Cons -> xs.tail
        }

        /**
         * Using the same idea as in the previous exercise, implement the function setHead
         * for replacing the first element of a List with a different value.
         */
        fun <A> setHead(xs: List<A>, x: A): List<A> = when (xs) {
            is Nil -> error("Cannot replace `head` of a Nil list")
            is Cons -> Cons(x, xs.tail)
        }

        /**
         * Generalize tail to the function drop,
         * which removes the first n elements from a list.
         * Note that this function takes time proportional
         * only to the number of elements being dropped—you don’t need to
         * make a copy of the entire List.
         */
        fun <A> drop(l: List<A>, n: Int): List<A> = if (n == 0) l
        else when (l) {
            is Cons -> drop(l.tail, n - 1)
            is Nil -> error("Cannot drop more elements than in list")
        }

        /**
         * A more surprising example of data sharing is the following function,
         * which adds all the elements of one list to the end of another.
         */
        fun <A> append(a1: List<A>, a2: List<A>): List<A> = when (a1) {
            is Nil -> a2
            is Cons -> Cons(a1.head, append(a1.tail, a2))
        }

        /**
         * Implement dropWhile, which removes elements from the
         * List prefix as long as they match a predicate.
         */
        fun <A> dropWhile(l: List<A>, f: (A) -> Boolean): List<A> = when (l) {
            is Nil -> l
            is Cons -> if (f(l.head)) dropWhile(l.tail, f) else l
        }


        /**
         * Implement a function, init, that returns a List consisting of all but the last
         * element of a List. So, given List(1, 2, 3, 4), init should return List(1, 2, 3).
         */
        fun <A> init(l: List<A>): List<A> = when (l) {
            is Nil -> error("Cannot init Nil list")
            is Cons -> {
                if (l.tail == Nil) {
                    Nil
                } else Cons(l.head, init(l.tail))
            }
        }

        fun <A> length(xs: List<A>): Int = foldRight(xs, 0, { _, acc -> 1 + acc })

        fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B = when (xs) {
            is Nil -> z
            is Cons -> f(xs.head, foldRight(xs.tail, z, f))
        }

        tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B = when (xs) {
            is Nil -> z
            is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
        }

        /**
         * The foldLeftR function processes the list from left to right by using
         * foldRight and a technique called function composition.
         * The core idea is to transform the accumulator logic into a series of nested function calls
         *
         * Implementation: Uses foldRight with function composition to achieve a left fold.
         * Order: Processes the list from right to left but accumulates from left to right.
         * Complexity: Involves nested functions and may be less efficient.
         *
         *  Example:
         *         List: [1, 2, 3]
         *         Function: (b, a) -> b + a
         *         Calculation:
         *             Build a function composition: { b -> f(f(f(b, 1), 2), 3) }
         *             Apply to z = 0: f(f(0 + 1, 2), 3) = f(1 + 2, 3) = 3 + 3 = 6
         *         Result: 6
         */
        fun <A, B> foldLeftR(xs: List<A>, z: B, f: (B, A) -> B): B =
            foldRight(xs, { b: B -> b }, { a, g ->
                { b ->
                    g(f(b, a))
                }
            })(z)


        /**
         *
         *  Implementation: Uses foldLeft with function composition to achieve a right fold.
         *  Order: Processes the list from left to right but accumulates from right to left.
         *  Complexity: Involves nested functions and may be less efficient.
         *
         *  Example:
         *         List: [1, 2, 3]
         *         Function: (a, b) -> a + b
         *         Calculation:
         *             Build a function composition: { b -> f(1, f(2, f(3, b))) }
         *             Apply to z = 0: f(1, f(2, f(3, 0))) = f(1, f(2, 3)) = f(1, 5) = 6
         *         Result: 6
         */
        fun <A, B> foldRightL(xs: List<A>, z: B, f: (A, B) -> B): B =
            foldLeft(xs, { b: B -> b }, { g, a ->
                { b ->
                    g(f(a, b))
                }
            })(z)

        fun <A, B> foldLeftRDemystified(
            ls: List<A>,
            acc: B,
            combiner: (B, A) -> B,
        ): B {

            val identity: Identity<B> = { b: B -> b }

            val combinerDelayer: (A, Identity<B>) -> Identity<B> =
                { a: A, delayedExec: Identity<B> ->
                    { b: B ->
                        delayedExec(combiner(b, a))
                    }
                }

            val chain: Identity<B> = foldRight(ls, identity, combinerDelayer)

            return chain(acc)
        }

        fun <A> reverse(xs: List<A>): List<A> =
            foldLeft(xs, empty(), { t: List<A>, h: A -> Cons(h, t) })

        fun <A> appendFoldR(a1: List<A>, a2: List<A>): List<A> =
            foldRight(a1, a2, { x, y -> Cons(x, y) })

        fun <A> appendFoldL(a1: List<A>, a2: List<A>): List<A> =
            foldLeft(reverse(a1), a2, { y, x -> Cons(x, y) })

        fun <A> concatR1(xxs: List<List<A>>): List<A> = foldRight(xxs, empty(), { a, b ->
            append(a, b)
        })

        fun <A> concatR2(xxs: List<List<A>>): List<A> =
            foldRight(
                xxs,
                empty(),
                { xs1: List<A>, xs2: List<A> ->
                    foldRight(xs1, xs2, { a, ls -> Cons(a, ls) })
                })

        fun <A> concatL(xxs: List<List<A>>): List<A> = foldLeft(xxs, empty(), { b, a ->
            append(b, a)
        })

        fun <A, B> map(xs: List<A>, f: (A) -> B): List<B> =
            foldRightL(xs, empty()) { a: A, xa: List<B> ->
                Cons(f(a), xa)
            }
    }

}

// Nil implementation of List
data object Nil : List<Nothing>()

//  Cons = construct implementation of List
data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()

fun sum(xs: List<Int>): Int = foldRight(xs, 0, { a, b -> a + b })

fun product(xs: List<Double>): Double = foldRight(xs, 1.0, { a, b -> a * b })

fun sumL(xs: List<Int>): Int = foldLeft(xs, 0, { b, a -> a + b })

fun sumR(xs: List<Int>): Int = foldRight(xs, 0, { a, b -> a + b })

fun productL(xs: List<Double>): Double = foldLeft(xs, 1.0, { a, b -> a * b })

fun productR(xs: List<Double>): Double = foldRight(xs, 1.0, { a, b -> a * b })

fun lengthL(xs: List<Int>): Int = foldLeft(xs, 0, { acc, _ -> acc + 1 })

fun increment(xs: List<Int>): List<Int> = foldRight(
    xs, empty(), { a, b -> Cons(a + 1, b) }
)

fun doubleToString(xs: List<Double>): List<String> = foldRight(xs, empty(), { a, b ->
    Cons(a.toString(), b)
})

val f = { x: Int, y: List<Int> -> Cons(x, y) }
val z = Nil as List<Int>

val traceFoldRight = {
    foldRight(List.of(1, 2, 3), z, f)
    Cons(1, foldRight(List.of(2, 3), z, f))
    Cons(1, Cons(2, foldRight(List.of(3), z, f)))
    Cons(1, Cons(2, Cons(3, foldRight(List.empty(), z, f))))
    Cons(1, Cons(2, Cons(3, Nil)))
}