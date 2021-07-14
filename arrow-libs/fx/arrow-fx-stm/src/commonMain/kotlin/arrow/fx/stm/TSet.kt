package arrow.fx.stm

import arrow.fx.stm.internal.Hamt
import arrow.fx.stm.internal.newHamt

public fun <A> STM.newTSet(fn: (A) -> Int): TSet<A> = TSet(newHamt(), fn)
public fun <A> STM.newTSet(): TSet<A> = newTSet { it.hashCode() }

/**
 *
 * A [TSet] is a concurrent transactional implementation of a hashset.
 *
 * Based on a Hash-Array-Mapped-Trie implementation. While this does mean that a read may take up to 5 steps to be resolved (depending on how
 *  well distributed the hash function is), it also means that structural changes can be isolated and thus do not increase contention with
 *  other transactions. This effectively means concurrent access to different values is unlikely to interfere with each other.
 *
 * > Hash conflicts are resolved by chaining.
 *
 * ## Creating a [TSet]
 *
 * Depending on whether or not you are in a transaction you can use either [STM.newTSet] or [TSet.new] to create a new [TSet].
 *
 * There are a few alternatives because [TSet] can be supplied a custom hash strategy. If no argument is given it defaults to [Any.hashCode].
 *
 * ## Adding elements to the set
 *
 * Adding an element can be achieved by using either [STM.insert] or its alias [STM.plusAssign]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSet
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tset = TSet.new<String>()
 *   atomically {
 *     tset.insert("Hello")
 *     tset += "World"
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * ## Removing an element from the set
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSet
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tset = TSet.new<String>()
 *   atomically {
 *     tset.insert("Hello")
 *     tset.remove("Hello")
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * ## Checking for membership
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSet
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tset = TSet.new<String>()
 *   val result = atomically {
 *     tset.insert("Hello")
 *     tset.member("Hello")
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * ## Where are operations like `isEmpty` or `size`?
 *
 * This is a design tradeoff. It is entirely possible to track size however this usually requires one additional [TVar] for size and
 *  almost every operation would modify that. That will lead to contention and thus decrease performance.
 *
 * Should this feature interest you and performance is not as important please open an issue. It is most certainly possible to add another version
 *  of [TSet] that keeps track of its size.
 *
 */
public data class TSet<A>internal constructor(internal val hamt: Hamt<A>, internal val hashFn: (A) -> Int) {
  public companion object {
    public suspend fun <A> new(fn: (A) -> Int): TSet<A> = TSet(Hamt.new(), fn)
    public suspend fun <A> new(): TSet<A> = new { it.hashCode() }
  }
}
