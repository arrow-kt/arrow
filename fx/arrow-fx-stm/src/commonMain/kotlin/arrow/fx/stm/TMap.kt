package arrow.fx.stm

import arrow.fx.stm.internal.Hamt
import arrow.fx.stm.internal.newHamt

public fun <K, V> STM.newTMap(fn: (K) -> Int): TMap<K, V> = TMap(newHamt(), fn)
public fun <K, V> STM.newTMap(): TMap<K, V> = newTMap { it.hashCode() }

/**
 * A [TMap] is a concurrent transactional implementation of a key value hashmap.
 *
 * Based on a Hash-Array-Mapped-Trie implementation. While this does mean that a read may take up to 5 steps to be resolved (depending on how
 *  well distributed the hash function is), it also means that structural changes can be isolated and thus do not increase contention with
 *  other transactions. This effectively means concurrent access to different values is unlikely to interfere with each other.
 *
 * > Hash conflicts are resolved by chaining.
 *
 * ## Creating a [TMap]
 *
 * Depending on whether or not you are in a transaction you can use either [STM.newTMap] or [TMap.new] to create a new [TMap].
 *
 * There are a few alternatives because [TMap] can be supplied a custom hash strategy. If no argument is given it defaults to [Any.hashCode].
 *
 * ## Reading an element with a key
 *
 * Reading from a [TMap] can be done using either [STM.lookup] or its alias [STM.get].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMap
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmap = TMap.new<Int, String>()
 *   val result = atomically {
 *     tmap.set(1, "Hello")
 *     tmap[2] = "World"
 *
 *     tmap.lookup(1) + tmap[2]
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * > If the key is not present [STM.lookup] will not retry, instead it returns `null`.
 *
 * ## Inserting a value
 *
 * Inserting can be done using either [STM.insert] or its alias [STM.set]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMap
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmap = TMap.new<Int, String>()
 *   atomically {
 *     tmap.insert(1, "Hello")
 *     tmap[2] = "World"
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * Another option when adding elements is to use [STM.plusAssign]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMap
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmap = TMap.new<Int, String>()
 *   atomically {
 *     tmap += (1 to "Hello")
 *     tmap += (2 to "World")
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * ## Updating an existing value [TMap]:
 *
 * Using [STM.update] it is possible to update an existing value of a [TMap]. If the value is not present it does nothing.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMap
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmap = TMap.new<Int, String>()
 *   val result = atomically {
 *     tmap[1] = "Hello"
 *     tmap[2] = "World"
 *
 *     tmap.update(1) { it.reversed() }
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * ## Checking membership
 *
 * Using [STM.member] it is possible to check if a [TMap] contains a value for a key:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMap
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmap = TMap.new<Int, String>()
 *   val result = atomically {
 *     tmap[1] = "Hello"
 *     tmap.member(1)
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * ## Removing a value from a [TMap]
 *
 * Removing is done by using [STM.remove]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMap
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmap = TMap.new<Int, String>()
 *   atomically {
 *     tmap[1] = "Hello"
 *
 *     tmap.remove(1)
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * ## Where are operations like `isEmpty` or `size`?
 *
 * This is a design tradeoff. It is entirely possible to track size however this usually requires one additional [TVar] for size and
 *  almost every operation would modify that. That will lead to contention and thus decrease performance.
 *
 * Should this feature interest you and performance is not as important please open an issue. It is most certainly possible to add another version
 *  of [TMap] that keeps track of its size.
 *
 */
public data class TMap<K, V>internal constructor(internal val hamt: Hamt<Pair<K, V>>, internal val hashFn: (K) -> Int) {
  public companion object {
    public suspend fun <K, V> new(fn: (K) -> Int): TMap<K, V> = TMap(Hamt.new(), fn)
    public suspend fun <K, V> new(): TMap<K, V> = new { it.hashCode() }
  }
}
