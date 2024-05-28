import kotlinx.coroutines.sync.Mutex

class FineSyncDrova<K : Comparable<K>, V> : AbstractDrova<K, V, FineNode<K, V>>() {
    private val rootMutex = Mutex()

    override suspend fun search(key: K): V? {
        root?.lock()
        return root?.search(key)
    }

    override suspend fun add(key: K, value: V) {
        rootMutex.lock()

        if (root != null) {
            root?.lock()
            rootMutex.unlock()
            root?.add(key, value)
        } else {
            root = FineNode(key, value)
            rootMutex.unlock()
        }
    }

    override suspend fun remove(key: K) {
        rootMutex.lock()
        if (root == key) {
            root?.lock()
            root = root?.remove(root ?: throw NullPointerException(), key)
            rootMutex.unlock()
        } else {
            root?.lock()
            rootMutex.unlock()
            root = root?.remove(root ?: throw NullPointerException(), key)
        }
    }
}