package fp.functional_programming_in_kotin.ch3

import fp.functional_programming_in_kotin.ch3.List.Companion.append
import fp.functional_programming_in_kotin.ch3.List.Companion.drop
import fp.functional_programming_in_kotin.ch3.List.Companion.dropWhile
import fp.functional_programming_in_kotin.ch3.List.Companion.init
import fp.functional_programming_in_kotin.ch3.List.Companion.setHead
import fp.functional_programming_in_kotin.ch3.List.Companion.tail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ListTest : WordSpec({
    "list tail" should {
        "return the the tail when present" {
            tail(List.of(1, 2, 3, 4, 5)) shouldBe
                    List.of(2, 3, 4, 5)
        }

        "throw an illegal state exception when no tail is present" {
            shouldThrow<IllegalStateException> {
                tail(Nil)
            }
        }
    }

    "list setHead" should {
        "return a new List with a replaced head" {
            setHead(List.of(1, 2, 3, 4, 5), 6) shouldBe
                    List.of(6, 2, 3, 4, 5)
        }

        "throw an illegal state exception when no head is present" {
            shouldThrow<IllegalStateException> {
                setHead(Nil, 6)
            }
        }
    }

    "list drop" should {
        "drop a given number of elements within capacity" {
            drop(List.of(1, 2, 3, 4, 5), 3) shouldBe List.of(4, 5)
        }

        "drop a given number of elements up to capacity" {
            drop(List.of(1, 2, 3, 4, 5), 5) shouldBe Nil
        }

        """throw an illegal state exception when dropped elements
            exceed capacity""" {
            shouldThrow<IllegalStateException> {
                drop(List.of(1, 2, 3, 4, 5), 6)
            }
        }
    }

    "list append" should {
        val x1 = List.of(1, 2)
        val x2 = List.of(3, 4)

        "merge two list" {
            append(x1, x2) shouldBe List.of(1, 2, 3, 4)
        }

    }

    "list dropWhile" should {
        val xs = List.of(1, 2, 3, 4, 5)
        "drop elements until predicate is no longer satisfied" {
            dropWhile(xs) { it < 4 } shouldBe List.of(4, 5)
        }

        "drop no elements if predicate never satisfied" {
            dropWhile(xs) { it == 100 } shouldBe xs
        }

        "drop all elements if predicate always satisfied" {
            dropWhile(xs) { it < 100 } shouldBe List.of()
        }

        "return Nil if input is empty" {
            dropWhile(List.empty<Int>()) { it < 100 } shouldBe Nil
        }
    }

    "list init" should {
        "drop last element" {
            init(List.of(1, 2, 3, 4, 5)) shouldBe List.of(1, 2, 3, 4)
        }

        "return single element list contains only two elements" {
            init(List.of(1, 2)) shouldBe List.of(1)
        }

        "return empty if list contains only one element" {
            init(List.of(1)) shouldBe List.of()
        }

        "throw IllegalStateException when init empty list" {
            shouldThrow<IllegalStateException> {
                init(List.empty<Int>())
            }
        }

    }

})