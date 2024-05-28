
class HardNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: HardNode<K, V>? = null,
    right: HardNode<K, V>? = null
) : AbstractNode<K, V, HardNode<K, V>>(key, value, left, right) {

    override suspend fun search(key: K): V? {
        if (this.key == key) {
            return this.value
        }
        else if (this.key < key){
            return right?.search(key)
        }
        else return left?.search(key)
    }

    override suspend fun add(key: K, value: V) {
        if (this.key == key)  println("Node with key" + key + "already exists")
        else if (this.key < key)
            if (right == null) right = HardNode(key, value) else right?.add(key, value)
        else
            if (left == null) left = HardNode(key, value) else left?.add(key, value)
    }

    override suspend fun remove(childTree: HardNode<K, V>, key: K): HardNode<K, V>? {
        if (this.key == key) {
            if (left == null && right == null){return null}
            else if (left == null){return right}
            else if (right == null){return left}
            else {
                val minNode = right?.min() ?: throw NullPointerException()
                this.key = minNode.key
                this.value = minNode.value
                this.right = right?.remove(right ?: throw NullPointerException(), minNode.key)
                return this
            }
        } else {
            if (left == null && right == null)
                println("Node with key $key doesn't exist")

            if (this.key < key)
                this.right = right?.remove(right ?: throw NullPointerException(), key)
            else
                this.left = left?.remove(left ?: throw NullPointerException(), key)
            return childTree
        }
    }
}