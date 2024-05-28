import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals


abstract class GeneralTests<N : AbstractNode<Int, String, N>, T : AbstractDrova<Int, String, N>>
    (
    private val treeFactory: () -> T,
    private val nodesCount: Int = 500000
) {
    private fun time() = Random.nextLong(100)

    private fun randomKeys(count: Int) = (0 until count).shuffled(Random).take(nodesCount)

    /**
     * Add some elements in parallel
     */
    @Test
    fun `Parallel adding`() {
        val tree: T = treeFactory()
        val nodeKeysToAdd = randomKeys(nodesCount)
        runBlocking {
            coroutineScope {
                repeat(nodesCount) {
                    launch(Dispatchers.Default) {
                        delay(time())
                        tree.add(nodeKeysToAdd[it], nodeKeysToAdd[it].toString())
                    }
                }
            }
        }
        runBlocking {
            for (i in nodeKeysToAdd) {
                assertEquals(i.toString(), tree.search(i))
            }
        }

    }
    /**
     * Create tree and remove all nodes
     */
    @Test
    fun `Parallel tree nodes removing`() {
        val tree = treeFactory()
        var nodeKeys = randomKeys(nodesCount)

        runBlocking {
            repeat(nodesCount) {
                tree.add(nodeKeys[it], nodeKeys[it].toString())
            }
        }
        runBlocking {
            nodeKeys = nodeKeys.shuffled(Random)
            repeat(nodesCount) {
                launch(Dispatchers.Default) {
                    delay(time())
                    tree.remove(nodeKeys[it])
                }
            }
        }
        runBlocking {
            for (key in nodeKeys)
                assertEquals(null, tree.search(key))
        }
    }

    /**
     * Create tree and remove any nodes
     */
    @Test
    fun `Parallel tree nodes removing #2`() {
        val tree = treeFactory()
        var nodeKeys = randomKeys(nodesCount)

        runBlocking {
            repeat(nodesCount) {
                tree.add(nodeKeys[it], nodeKeys[it].toString())
            }
        }

        val notRemove = nodeKeys.shuffled(Random).take(nodesCount.div(10))

        // Remove some elements
        runBlocking {
            // to remove unevenly
            nodeKeys = nodeKeys.shuffled(Random)
            repeat(nodesCount) {
                launch(Dispatchers.Default) {
                    delay(time())
                    if (nodeKeys[it] !in notRemove)
                        tree.remove(nodeKeys[it])
                }

            }
        }

        runBlocking {
            for (i in nodeKeys) {
                if (i in notRemove)
                    assertEquals(i.toString(), tree.search(i))
                else
                    assertEquals(null, tree.search(i))

            }
        }

    }

    /**
     * Creates a tree, adds and removes elements from it
     */
    @Test
    fun `Parallel adding & removing`() {
        val tree = treeFactory()
        val keys = randomKeys(nodesCount)
        // half of elements for start tree
        val half = nodesCount.div(2)
        val startNodes = keys.take(half)
        val newNodes = keys.takeLast(half)

        // Create tree
        runBlocking {
            repeat(half) {
                tree.add(startNodes[it], startNodes[it].toString())
            }
        }

        runBlocking {
            repeat(half) {
                launch(Dispatchers.Default) {
                    delay(time())
                    tree.add(newNodes[it], newNodes[it].toString())
                }
                launch(Dispatchers.Default) {
                    delay(time())
                    tree.remove(startNodes[it])
                }
            }
        }

        runBlocking {
            for (key in newNodes) {
                assertEquals(key.toString(), tree.search(key), "(Node creating)")
            }
            for (key in startNodes) {
                assertEquals(null, tree.search(key), "(Node removing)")
            }
        }
    }

}