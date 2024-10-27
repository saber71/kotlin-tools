package heraclius.tools

object Tree {
    interface LeafNode {
        val isLeaf: Boolean

        fun getName(): String

        fun getAttributes(): List<NodeAttribute>

        fun getParent(): LeafNode?
    }

    interface Node : LeafNode {
        fun getChildren(): List<LeafNode>
    }

    data class NodeAttribute(val label: String, val value: Any)

    fun getPath(node: LeafNode): List<LeafNode> {
        val path = ArrayList<LeafNode>()
        var currentNode: LeafNode = node
        while (true) {
            path.add(currentNode)
            val parent = currentNode.getParent()
            if (parent == null) break
            currentNode = parent
        }
        return path.reversed()
    }

    fun getPathString(node: LeafNode, separator: String = "/"): String {
        return getPath(node).joinToString(separator) { it.getName() }
    }
}
