
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


    @Suppress("UNCHECKED_CAST")
    open suspend fun min(): N = this.left?.min() ?: (this as N)
}