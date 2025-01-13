package fp.functional_programming_in_kotin.ch3

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
        fun <A> setHead(xs: List<A>, x: A): List<A> =
            when (xs) {
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
        fun <A> drop(l: List<A>, n: Int): List<A> =
            if (n == 0) l
            else when (l) {
                is Cons -> drop(l.tail, n - 1)
                is Nil -> error("Cannot drop more elements than in list")
            }

        /**
         * A more surprising example of data sharing is the following function,
         * which adds all the elements of one list to the end of another.
         */
        fun <A> append(a1: List<A>, a2: List<A>): List<A> =
            when (a1) {
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
        fun <A> init(l: List<A>): List<A> =
            when (l) {
                is Nil -> error("Cannot init Nil list")
                is Cons -> {
                    if (l.tail == Nil) {
                        Nil
                    } else Cons(l.head, init(l.tail))
                }
            }

    }

}


// Nil implementation of List
data object Nil : List<Nothing>()

//  Cons = construct implementation of List
data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()
