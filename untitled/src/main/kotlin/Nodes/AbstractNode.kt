
/**
 * Tree node
 */
abstract class AbstractNode<K : Comparable<K>, V, N : AbstractNode<K, V, N>>(
    var key: K,
    var value: V,
    var left: N? = null,
    var right: N? = null
) {
    abstract suspend fun add(key: K, value: V)
    abstract suspend fun search(key: K): V?
    abstract suspend fun remove(childTree: N, key: K): N?

//    override fun toString(): String = "($key, $value)"

    @Suppress("UNCHECKED_CAST")
    open suspend fun min(): N = this.left?.min() ?: (this as N)


//    fun buildString(sb: StringBuilder, padding: String = "", pointer: String = "") {
//        sb.append(padding)
//        sb.append(pointer)
//        sb.append("($key,$value)")
//        sb.append("\n")
//
//        val paddingBuilder = StringBuilder(padding)
//        paddingBuilder.append("│  ")
//
//        val paddingForBoth = paddingBuilder.toString()
//        val pointerForRight = "└──"
//        val pointerForLeft = if (right != null) "├──" else "└──"
//
//        left?.buildString(sb, paddingForBoth, pointerForLeft)
//        right?.buildString(sb, paddingForBoth, pointerForRight)
//
//    }
}