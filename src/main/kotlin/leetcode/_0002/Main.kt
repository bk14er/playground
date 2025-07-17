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

fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
    if (l1 == null && l2 == null) {
        return null
    }
    val currentVal = (l1?.`val` ?: 0) + (l2?.`val` ?: 0)

    val current = ListNode(currentVal % 10)
    val carry = currentVal / 10

    if (carry > 0) {
        current.next =
            addTwoNumbers(l1?.next, l2?.next?.apply { `val` += carry } ?: ListNode(carry))
    } else {
        current.next = addTwoNumbers(l1?.next, l2?.next?.apply { `val` += carry })
    }


    return current
}

fun main() {

    println(addTwoNumbers(ListNode(1), ListNode(2)))

    println(
        addTwoNumbers(
            ListNode(2).apply { next = ListNode(4).apply { next = ListNode(3) } },
            ListNode(5).apply { next = ListNode(6).apply { next = ListNode(4) } })
    )


    println(
        addTwoNumbers(
            ListNode(2).apply {
                next = ListNode(4).apply {
                    next = ListNode(9)
                }
            },
            ListNode(5).apply {
                next = ListNode(6).apply {
                    next = ListNode(4).apply {
                        next = ListNode(9)
                    }
                }
            })
    )


}