package leetcode._0002

class ListNode(var `val`: Int) {
    var next: ListNode? = null

    override fun toString(): String {
        val result = StringBuilder()
        var current: ListNode? = this

        while (current != null) {
            result.append(current.`val`)
            if (current.next != null) {
                result.append(" -> ")
            }
            current = current.next
        }

        return result.toString()
    }

}

fun addTwoNumbersRecursive(l1: ListNode?, l2: ListNode?): ListNode? {
    if (l1 == null && l2 == null) {
        return null
    }
    val currentVal = (l1?.`val` ?: 0) + (l2?.`val` ?: 0)

    val current = ListNode(currentVal % 10)
    val carry = currentVal / 10

    if (carry > 0) {
        current.next =
            addTwoNumbersRecursive(l1?.next, l2?.next?.apply { `val` += carry } ?: ListNode(carry))
    } else {
        current.next = addTwoNumbersRecursive(l1?.next, l2?.next?.apply { `val` += carry })
    }


    return current
}

fun addTwoNumbersIterative(l1: ListNode?, l2: ListNode?): ListNode? {
    val dummyHead = ListNode(0)
    var current = dummyHead

    var carry = 0
    var it1 = l1
    var it2 = l2

    while (carry > 0 || it1 != null || it2 != null) {
        val sum = (it1?.`val` ?: 0) + (it2?.`val` ?: 0)  + carry
        carry = sum / 10
        val digit = (sum % 10)

        current.next = ListNode(digit)
        current = current.next!!

        it1 = it1?.next
        it2 = it2?.next
    }

    return dummyHead.next
}

fun main() {
    println(addTwoNumbersRecursive(ListNode(1), ListNode(2)))
    println(addTwoNumbersIterative(ListNode(1), ListNode(2)))
    println()

    println(
        addTwoNumbersRecursive(
            ListNode(2).apply {
                next = ListNode(4).apply { next = ListNode(3) }
            },
            ListNode(5).apply { next = ListNode(6).apply { next = ListNode(4) } })
    )
    println(
        addTwoNumbersIterative(
            ListNode(2).apply {
                next = ListNode(4).apply { next = ListNode(3) }
            },
            ListNode(5).apply { next = ListNode(6).apply { next = ListNode(4) } })
    )
    println()


    println(
        addTwoNumbersRecursive(ListNode(2).apply {
            next = ListNode(4).apply {
                next = ListNode(9)
            }
        }, ListNode(5).apply {
            next = ListNode(6).apply {
                next = ListNode(4).apply {
                    next = ListNode(9)
                }
            }
        })
    )
    println(
        addTwoNumbersIterative(ListNode(2).apply {
            next = ListNode(4).apply {
                next = ListNode(9)
            }
        }, ListNode(5).apply {
            next = ListNode(6).apply {
                next = ListNode(4).apply {
                    next = ListNode(9)
                }
            }
        })
    )
    println()

}