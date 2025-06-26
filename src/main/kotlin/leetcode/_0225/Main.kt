package leetcode._0225

import java.util.LinkedList

/**
 * Your MyStack object will be instantiated and called as such:
 * var obj = MyStack()
 * obj.push(x)
 * var param_2 = obj.pop()
 * var param_3 = obj.top()
 * var param_4 = obj.empty()
 */

class MyStack(private val queue: LinkedList<Int> = LinkedList<Int>()) {

    fun push(x: Int) {
        queue.push(x)
    }

    fun pop(): Int = queue.pop()

    fun top(): Int = queue.first

    fun empty(): Boolean = queue.isEmpty()

}


fun main() {
    val obj = MyStack()
    obj.push(1)
    var param_2 = obj.pop()
    var param_3 = obj.top()
    var param_4 = obj.empty()
}