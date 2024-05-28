
import kotlinx.coroutines.sync.Mutex


class OptimisticDrova <K : Comparable<K>, V> : AbstractDrova<K, V, OptimisticNode<K, V>>() {
    private val rootMutex = Mutex()

    private fun validate(node: OptimisticNode<K, V>?): Boolean {
        var currentNode = root ?: return false
        node ?: return false

        while (currentNode != node) {
            if (currentNode.key < node.key){currentNode = currentNode.right ?: return false}
            else currentNode = currentNode.left ?: return false
        }

        return true
    }

    override suspend fun search(key: K): V? = root?.search(key)


    override suspend fun add(key: K, value: V) {
        if (root == null) {
            rootMutex.lock()
            if (root == null) {
                root = OptimisticNode(key, value) { node -> validate(node) }
                rootMutex.unlock()
            } else {
                add(key, value)
                rootMutex.unlock()
            }
        } else {
            try {
                root?.add(key, value)
            } catch (_: IllegalThreadStateException) {
                add(key, value)
            }
        }
    }

    override suspend fun remove(key: K) {
        var childNode = root ?: throw IllegalStateException("Root is null")
        var parentNode: OptimisticNode<K, V>? = null
        while (childNode.key != key) {
            parentNode = childNode
            val res : OptimisticNode<K, V>?
            if (childNode.key < key){
                 res = childNode.right
            }
            else {
                res = childNode.left
            }
            if (res == null) {
                if (root != null) {
                    remove(key)
                    return
                } else
                    println("Node with key$key does not exists")
            } else
                childNode = res
        }
        if (parentNode == null) {
            childNode.lock()
            if (root?.key == childNode.key) {
                try {
                    childNode.also { root = it.remove(it, key) }
                    childNode.unlock()
                } catch (_: IllegalThreadStateException) {
                    childNode.unlock()
                    remove(key)
                    return
                }
            } else {
                childNode.unlock()
                remove(key)
            }
        } else {
            parentNode.lock(); childNode.lock()
            val verify = validate(parentNode) && (parentNode.right == childNode || parentNode.left == childNode) && childNode.key == key
            if (verify) {
                try {
                    if (parentNode.key < key)
                        childNode.also { parentNode.right = it.remove(it, key) }
                    else
                        childNode.also { parentNode.left = it.remove(it, key) }
                    childNode.unlock(); parentNode.unlock()
                } catch (_: IllegalThreadStateException) {
                    childNode.unlock(); parentNode.unlock()
                    remove(key)
                    return
                }
            } else {
                childNode.unlock(); parentNode.unlock()
                remove(key)
            }
        }
    }
}