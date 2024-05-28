//package org.example.Nodes

import kotlinx.coroutines.sync.Mutex

class FineNode<K : Comparable<K>, V>(
    key: K,
    value: V,
    left: FineNode<K, V>? = null,
    right: FineNode<K, V>? = null
) : AbstractNode<K, V, FineNode<K, V>>(key, value, left, right) {

    private val mutex = Mutex()

    suspend fun lock() = mutex.lock()
    private fun unlock() = mutex.unlock()


    override suspend fun search(key: K): V? {
        return helperSearch(key)
    }

    suspend fun helperSearch(key: K): V? {
        if(this.key == key){
            this.unlock()
            return this.value
        }
        else if(this.key > key){
            left?.lock()
            this.unlock()
            return left?.helperSearch(key)
        }
        else{
            right?.lock()
            this.unlock()
            return right?.helperSearch(key)
        }
    }

    override suspend fun add(key: K, value: V) {
        if (this.key == key) {
            this.unlock()
            println("Node with key" + key + "already exists")
        } else if (this.key < key) {
            if (right == null) {
                right = FineNode(key, value)
                this.unlock()
            } else {
                right?.lock()
                this.unlock()
                right?.add(key, value)
            }

        } else {
            if (left == null) {
                left = FineNode(key, value)
                this.unlock()
            } else {
                left?.lock()
                this.unlock()
                left?.add(key, value)
            }

        }
    }

    override suspend fun remove(childTree: FineNode<K, V>, key: K): FineNode<K, V>? {
        if (this.key == key) {
            if (left == null && right == null){
                this.unlock()
                return null
            }
            else if(right == null){
                this.unlock()
                return left
            }
            else if(left == null){
                this.unlock()
                return right
            }
            else{
                right?.lock()
                val minimalNode = right?.min() ?: throw NullPointerException()
                right = right?.remove(right ?: throw NullPointerException(), minimalNode.key)
                this.key = minimalNode.key
                this.value = minimalNode.value
                this.unlock()
                return this
            }
        } else {
            if (left == null && right == null) {
                this.unlock()
                println("Node with key" + key + "doesn't exist")
            }

            if (this.key > key) {
                left?.lock()
                this.unlock()
                left = left?.remove(left ?: throw NullPointerException(), key)
            } else {
                right?.lock()
                this.unlock()
                right = right?.remove(right ?: throw NullPointerException(), key)
            }
            return childTree
        }

    }

}