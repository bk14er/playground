package leetcode._0257


class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class Solution {
    fun binaryTreePaths(root: TreeNode?): List<String> {
        val paths = mutableListOf<String>()
        if (root != null) {
            constructPaths(root, StringBuilder(), paths)
        }
        return paths
    }


    private fun constructPaths(node: TreeNode, path: StringBuilder, paths: MutableList<String>) {
        val len = path.length
        path.append(node.`val`)

        if (node.left == null && node.right == null) {
            // If it's a leaf node, add path to the result list
            paths.add(path.toString())
        } else {
            path.append("->")
            node.left?.let {
                constructPaths(it, path, paths)
            }
            node.right?.let {
                constructPaths(it, path, paths)
            }
        }

        // Restore the path to its previous state after recursion
        path.setLength(len)
    }

    /**
     * For the first branch:
     * 1 , left
     * 1 , 2, right
     * 1, 2, 5
     * reset
     *
     *
     *
     */

}

/**
 * 257. Binary Tree Paths
 * https://leetcode.com/problems/binary-tree-paths/description/?envType=problem-list-v2&envId=24vvbd7s
 */
fun main() {
    val _5 = TreeNode(5)
    val _3 = TreeNode(3)

    val _2 = TreeNode(2)
        .also { it.right = _5 }

    val _root = TreeNode(1)
        .also {
            it.left = _2
            it.right = _3
        }


    println(Solution().binaryTreePaths(_root))

}