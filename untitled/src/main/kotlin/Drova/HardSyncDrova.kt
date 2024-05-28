import kotlinx.coroutines.sync.Mutex

class HardSyncDrova<K : Comparable<K>, V> : AbstractDrova<K, V, HardNode<K, V>>() {
    private val rootMutex = Mutex()

    override suspend fun search(key: K): V? {
        rootMutex.lock()
        val res = root?.search(key)
        rootMutex.unlock()
        return res
    }

    override suspend fun add(key: K, value: V) {
        rootMutex.lock()
        if (root != null)
            root?.add(key, value)
        else
            root = HardNode(key, value)
        rootMutex.unlock()
    }

    override suspend fun remove(key: K) {
        rootMutex.lock()
        root = root?.remove(root ?: throw NullPointerException(), key)
        rootMutex.unlock()
    }

}