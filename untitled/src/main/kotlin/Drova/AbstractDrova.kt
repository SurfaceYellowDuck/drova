abstract class AbstractDrova <K : Comparable<K>, V, C : AbstractNode<K, V, C>> {
    protected var root: C? = null

    abstract suspend fun add(key: K, value: V)
    abstract suspend fun remove(key: K)
    abstract suspend fun search(key: K): V?
}