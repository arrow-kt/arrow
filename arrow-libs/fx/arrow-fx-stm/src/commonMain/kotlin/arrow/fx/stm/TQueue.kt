package arrow.fx.stm

import arrow.core.Predicate
import kotlin.js.JsName

public fun <A> STM.newTQueue(): TQueue<A> = TQueue(newTVar(PList.Nil), newTVar(PList.Nil))

/**
 * A [TQueue] is a transactional unbounded queue which can be written to and read from concurrently.
 *
 * The implementation uses two [TVar]'s containing lists. One for read and one for write access.
 *  Due to the semantics of [STM] this means a write to the queue will never invalidate or block a read and vice versa, making highly
 *  concurrent use possible.
 *
 * > In practice, if the read variable is empty, the two must swap contents but this operation is infrequent and thus can be ignored.
 *
 * ## Creating a [TQueue]
 *
 * Creating an empty queue can be done by using either [STM.newTQueue] or [TQueue.new] depending on whether or not you are in
 *  a transaction or not.
 *
 * ## Writing to the [TQueue]
 *
 * Writing to the end of the queue is done by using [STM.write]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   atomically {
 *     tq.write(2)
 *     // or alternatively
 *     tq += 4
 *   }
 *   //sampleEnd
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * It is also possible to write to the front of the queue, but since that accesses the read variable it can lead to worse overall performance:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   atomically {
 *     tq.write(1)
 *     tq.writeFront(2)
 *   }
 *   //sampleEnd
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * ## Reading items from a [TQueue]
 *
 * There are several different ways to read from a [TQueue], the most common one being [STM.read]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.write(2)
 *     tq.read()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * Should the queue be empty calling [STM.read] will cause the transaction to retry and thus wait for items to be added to the queue.
 *  This can be avoided using [STM.tryRead] instead:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.tryRead()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * [STM.read] also removes the read item from the queue. Alternatively [STM.peek] will leave the queue unchanged on a read:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.write(2)
 *
 *     tq.peek()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * As with [STM.read] [STM.peek] will retry should the queue be empty. The alternative [STM.tryPeek] is there to avoid that:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.tryPeek()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * It is also possible to read the entire list in one go using [STM.flush]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.write(2)
 *     tq.write(4)
 *
 *     tq.flush()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * ## Checking a queues size
 *
 * Checking if a queue is empty can be done by using either [STM.isEmpty] or [STM.isNotEmpty]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.isEmpty()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * Retrieving the actual size of a list can be done using [STM.size]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   val result = atomically {
 *     tq.size()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * > All three of these methods have to access both the write and read end of a [TQueue] and thus can increase contention. Use them sparingly!
 *
 * ## Removing elements from a [TQueue]
 *
 * It is also possible to remove elements from a [TQueue] using [STM.removeAll]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TQueue
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tq = TQueue.new<Int>()
 *   atomically {
 *     tq.write(0)
 *     tq.removeAll { it != 0 }
 *   }
 *   //sampleEnd
 *   println("Items in queue ${atomically { tq.flush() }}")
 * }
 * ```
 *
 * > This method also access both ends of the queue and thus should be used infrequently to avoid contention.
 */
public data class TQueue<A> internal constructor(
  internal val reads: TVar<PList<A>>,
  internal val writes: TVar<PList<A>>
) {
  public companion object {
    public suspend fun <A> new(): TQueue<A> = TQueue(TVar.new(PList.Nil), TVar.new(PList.Nil))
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
  public data class Cons<A>(val value: A, @JsName("_tail") val tail: PList<A>) : PList<A>()
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
