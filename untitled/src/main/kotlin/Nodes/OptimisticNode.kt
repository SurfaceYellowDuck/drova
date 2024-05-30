
import kotlinx.coroutines.sync.Mutex

class OptimisticNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: OptimisticNode<K, V>? = null,
    right: OptimisticNode<K, V>? = null,
    private val validate: (OptimisticNode<K, V>?) -> Boolean
) : AbstractNode<K, V, OptimisticNode<K, V>>(key, value, left, right) {

    private val mutex = Mutex()
    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()

    override suspend fun search(key: K): V? {
        return if (this.key == key) {
            value
        } else if (this.key < key) right?.search(key)
        else left?.search(key)
    }

    override suspend fun add(key: K, value: V) {
        if (this.key == key) throw IllegalArgumentException("Node with key $key already exists")
        else if (this.key < key) {
            if (right == null) {
                lock()
                if (right == null) {
                    right = OptimisticNode(key, value, validate = validate)
                    if (!validate(right)) {
                        right = null
                        unlock()
                        throw IllegalThreadStateException()
                    }
                    unlock()
                } else {
                    unlock()
                    throw IllegalThreadStateException()
                }
            } else right?.add(key, value)
        } else {
            if (left == null) {
                lock()
                if (left == null) {
                    left = OptimisticNode(key, value, validate = validate)
                    if (!validate(left)) {
                        left = null
                        unlock()
                        throw IllegalThreadStateException()
                    }
                    unlock()
                } else {
                    unlock()
                    throw IllegalThreadStateException()
                }
            } else left?.add(key, value)
        }
    }

    private suspend fun findMin(node: OptimisticNode<K, V>): Pair<K, V> {
        var parentNode: OptimisticNode<K, V> = node.right ?: throw NullPointerException()
        var childNode: OptimisticNode<K, V>? = parentNode.left

        while (childNode?.left != null) {
            parentNode = childNode
             childNode = childNode.left ?: throw IllegalThreadStateException()
        }

        parentNode.lock(); childNode?.lock()

        if (childNode == null && validate(parentNode) && parentNode.left == null) {
            val res = Pair(parentNode.key, parentNode.value)

            if (parentNode.right != null)
                node.right = parentNode.right
            else
                node.right = null
            parentNode.unlock()
            return res
        } else if (childNode != null && validate(parentNode) && parentNode.left == childNode && childNode.left == null) {
            val res = Pair(childNode.key, childNode.value)
            if (childNode.right != null) {
                parentNode.left = childNode.right
            } else {
                parentNode.left = null
            }
            childNode.unlock(); parentNode.unlock()
            return res
        } else {
            childNode?.unlock(); parentNode.unlock()
            throw IllegalThreadStateException()
        }
    }

    override suspend fun remove(childTree: OptimisticNode<K, V>, key: K): OptimisticNode<K, V>? {
        return if (this.key == key) {
            if (left == null && right == null)
                null
            else if (right == null)
                left
            else if (left == null)
                right
            else {
                val minNode = findMin(this)
                this.key = minNode.first
                this.value = minNode.second
                this
            }
        } else {
            throw IllegalThreadStateException()
        }
    }

}