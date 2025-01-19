package fp.functional_programming_in_kotin.ch3

import fp.functional_programming_in_kotin.ch3.List.Companion.append
import fp.functional_programming_in_kotin.ch3.List.Companion.appendFoldL
import fp.functional_programming_in_kotin.ch3.List.Companion.appendFoldR
import fp.functional_programming_in_kotin.ch3.List.Companion.concatL
import fp.functional_programming_in_kotin.ch3.List.Companion.concatR1
import fp.functional_programming_in_kotin.ch3.List.Companion.concatR2
import fp.functional_programming_in_kotin.ch3.List.Companion.drop
import fp.functional_programming_in_kotin.ch3.List.Companion.dropWhile
import fp.functional_programming_in_kotin.ch3.List.Companion.foldLeft
import fp.functional_programming_in_kotin.ch3.List.Companion.foldLeftR
import fp.functional_programming_in_kotin.ch3.List.Companion.foldLeftRDemystified
import fp.functional_programming_in_kotin.ch3.List.Companion.foldRightL
import fp.functional_programming_in_kotin.ch3.List.Companion.init
import fp.functional_programming_in_kotin.ch3.List.Companion.length
import fp.functional_programming_in_kotin.ch3.List.Companion.reverse
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

    "list length" should {
        "calculate the length" {
            length(List.of(1, 2, 3, 4, 5)) shouldBe 5
        }

        "calculate zero for an empty list" {
            length(Nil) shouldBe 0
        }
    }

    "list foldLeft" should {
        """apply a function f providing a zero accumulator from tail
            recursive position""" {
            foldLeft(
                List.of(1, 2, 3, 4, 5),
                0,
                { x, y -> x + y }) shouldBe 15
        }
    }

    "list sumL" should {
        "add all integers" {
            sumL(List.of(1, 2, 3, 4, 5)) shouldBe 15
        }
    }

    "list productL" should {
        "multiply all doubles" {
            productL(List.of(1.0, 2.0, 3.0, 4.0, 5.0)) shouldBe 120.0
        }
    }

    "list lengthL" should {
        "count the list elements" {
            lengthL(List.of(1, 2, 3, 4, 5)) shouldBe 5
        }
    }

    "list reverse" should {
        "reverse list elements" {
            reverse(List.of(1, 2, 3, 4, 5)) shouldBe
                    List.of(5, 4, 3, 2, 1)
        }
    }

    "list foldLeftR" should {
        "implement foldLeft functionality using foldRight" {
            foldLeftR(
                List.of(1, 2, 3, 4, 5),
                0,
                { x, y -> x + y }) shouldBe 15
            foldLeftRDemystified(
                List.of(1, 2, 3, 4, 5),
                0,
                { x, y -> x + y }) shouldBe 15
        }
    }

    "list foldRightL" should {
        "implement foldRight functionality using foldLeft" {
            foldRightL(
                List.of(1, 2, 3, 4, 5),
                0,
                { x, y -> x + y }) shouldBe 15
        }
    }

    "list appendFoldR" should {
        "append two lists to each other using foldRight" {
            appendFoldR(
                List.of(1, 2, 3),
                List.of(4, 5, 6)
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)
        }
    }
    "list appendFoldL" should {
        "append two lists to each other using foldLeft" {
            appendFoldL(
                List.of(1, 2, 3),
                List.of(4, 5, 6)
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)
        }
    }

    "list concat" should {
        "concatenate a list of lists into a single list" {
            concatR1(
                List.of(
                    List.of(1, 2, 3),
                    List.of(4, 5, 6)
                )
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)
            concatR2(
                List.of(
                    List.of(1, 2, 3),
                    List.of(4, 5, 6)
                )
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)

            concatL(
                List.of(
                    List.of(1, 2, 3),
                    List.of(4, 5, 6)
                )
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)
        }
    }

})