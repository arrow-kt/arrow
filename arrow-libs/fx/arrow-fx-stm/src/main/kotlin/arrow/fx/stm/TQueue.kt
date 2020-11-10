package arrow.fx.stm

import arrow.core.Predicate

fun <A> STM.newTQueue(): TQueue<A> = TQueue(newTVar(PList.Nil), newTVar(PList.Nil))

/**
 * A [TQueue] is a transactional unbounded queue which can be written to and read from concurrently.
 *
 * ## Creating a [TQueue]
 *
 * Creating an empty queue can be done by using either [STM.newTQueue] or [TQueue.new] depending on whether or not you are in
 *  a transaction or not.
 *
 * ## Concurrent reading and writing
 *
 * [TQueue] is implemented using two [TVar]'s. One for reading and one for writing.
 * This effectively means that writing and reading accesses two different variables and thus it never blocks each other.
 *
 * > In practice reads have to access the writes variable if they run out of elements to read but this is infrequent.
 */
data class TQueue<A> internal constructor(
  internal val reads: TVar<PList<A>>,
  internal val writes: TVar<PList<A>>
) {
  companion object {
    suspend fun <A> new(): TQueue<A> = TQueue(TVar.new(PList.Nil), TVar.new(PList.Nil))
  }
}

/**
 * Simple immutable and persistent linked list encoding to support constant time prepending, accessing and removing the head.
 *
 * Queues need lists to support two operations:
 * - uncons (head/tail) is what is used to read
 * either one of these:
 *  - cons and reverse is what is used to write
 *  - append
 *
 * A linked list has constant time uncons and cons and O(n) reverse. There is a datastructure that can avoid reverse while still
 *  being very fast for head access: A finger tree.
 * But finger trees are cache unfriendly and likely be a bit slower overall.
 *
 * Why this over alternatives?
 * - Kotlin stdlib List
 *  Implements mutability by copy and thus heavily pressures gc. It is fine for rare mutation but not for the use case of a
 *   queue under contention.
 * - Kotlinx immutable persistent lists
 *  Implements kotlins list interface but chooses the random access tradeoff over constant time head access.
 *   This means it will be faster for random access and mutation inside the list.
 *   But because of this choice it will be slower than uncons/cons on this linked list, and those methods are all we need to
 *   implement queues.
 */
internal sealed class PList<out A> {
  data class Cons<A>(val value: A, val tail: PList<A>) : PList<A>()
  object Nil : PList<Nothing>()

  /**
   * O(1)
   */
  fun head(): A = when (this) {
    Nil -> errorEmpty("Head")
    is Cons -> value
  }

  /**
   * O(1)
   */
  fun tail(): PList<A> = when (this) {
    Nil -> errorEmpty("Tail")
    is Cons -> tail
  }

  /**
   * O(1)
   */
  fun isEmpty(): Boolean = this == Nil

  /**
   * O(1)
   */
  fun isNotEmpty(): Boolean = this != Nil

  /**
   * O(n) and the entire list is copied
   */
  fun reverse(): PList<A> {
    var new: PList<A> = Nil
    var xs = this
    while (xs is Cons) {
      new = Cons(xs.value, new)
      xs = xs.tail
    }
    return new
  }

  /**
   * O(n)
   */
  inline fun filter(pred: Predicate<A>): PList<A> {
    var new: PList<A> = Nil
    var xs = this
    while (xs is Cons) {
      if (pred(xs.value)) new = Cons(xs.value, new)
      xs = xs.tail
    }
    return new.reverse()
  }

  /**
   * O(n)
   */
  inline fun filterNot(pred: Predicate<A>): PList<A> = filter { pred(it).not() }

  /**
   * O(n)
   */
  fun toList(): List<A> {
    val mutList = mutableListOf<A>()
    var xs = this
    while (xs is Cons) {
      mutList.add(xs.value)
      xs = xs.tail
    }
    return mutList
  }

  /**
   * O(n)
   */
  fun size(): Int {
    var sz = 0
    var xs = this
    while (xs is Cons) {
      sz += 1
      xs = xs.tail
    }
    return sz
  }
}

/**
 * O(1)
 */
internal fun <A> PList<A>.cons(a: A): PList<A> = PList.Cons(a, this)

private fun errorEmpty(name: String): Nothing = throw IllegalStateException("PList.$name was called on an empty list")
