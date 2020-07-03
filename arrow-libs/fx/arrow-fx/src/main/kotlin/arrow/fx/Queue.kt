package arrow.fx

import arrow.Kind
import arrow.Kind2
import arrow.core.Option
import arrow.fx.Queue.BackpressureStrategy
import arrow.fx.internal.ConcurrentQueue
import arrow.fx.typeclasses.Concurrent
import arrow.core.None
import arrow.core.Some

class ForQueue private constructor() {
  companion object
}

typealias QueueOf<F, A> = Kind2<ForQueue, F, A>
typealias QueuePartialOf<F> = Kind<ForQueue, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <F, A> QueueOf<F, A>.fix(): Queue<F, A> =
  this as Queue<F, A>

/**
 * [Dequeue] allows peeking and taking values from a [Queue], but doesn't allow offering values to the [Queue].
 * You can use [Dequeue] to restrict certain functions or layers of your applications to only consume values.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.*
 * import arrow.fx.typeclasses.*
 * import arrow.fx.extensions.fx
 * import kotlin.coroutines.EmptyCoroutineContext
 *
 * //sampleStart
 * suspend fun main(args: Array<String>): Unit = IO.fx {
 *   fun consumeInts(e: Dequeue<ForIO, Int>, max: Int): IOOf<Unit> =
 *     (0..max).toList().parTraverse(EmptyCoroutineContext) { i ->
 *       IO.sleep(i * 10.milliseconds).followedBy(
 *         e.take().effectMap { println("I took $it") }
 *       )
 *     }.void()
 *
 *   val queue = !Queue.unbounded<Int>()
 *   !consumeInts(queue, 1000).fork()
 *   !IO.sleep(4.seconds)
 * }.suspended()
 * //sampleEnd
 * ```
 *
 * @see Queue in the case your functions or layers are allowed to take and offer.
 * @see Dequeue in the case your functions or layers are only allowed to peek or take values.
 * */
interface Dequeue<F, A> {

  /**
   * Takes and removes a value from the [Queue], or semantically blocks until a value becomes available.
   *
   * ```kotlin:ank
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   *
   * //sampleStart
   * suspend fun main(args: Array<String>): Unit = IO.fx {
   *   val queue = !Queue.unbounded<Int>()
   *   val (join, _) = !queue.take().fork()
   *   !queue.offer(1) // Removing this offer makes, !join block forever.
   *   val res = !join // Join the blocking take, after we offered a value
   *   !effect { println(res) }
   * }.suspended()
   * //sampleEnd
   * ```
   *
   * @see [peek] for a function that doesn't remove the value from the [Queue].
   * @see [tryTake] for a function that does not semantically block but returns immediately with an [Option].
   */
  fun take(): Kind<F, A>

  /**
   * Attempts to take a value from the [Queue] if one is available, this method is guaranteed not to semantically block.
   * It returns immediately an [Option] with either [None] or a value wrapped in [Some].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   *
   * //sampleStart
   * suspend fun main(args: Array<String>): Unit = IO.fx {
   *   val queue = !Queue.unbounded<Int>()
   *   val none = !queue.tryTake()
   *   !queue.offer(1)
   *   val one = !queue.tryTake()
   *   val none2 = !queue.tryTake()
   *   !effect { println("none: $none, one $one, none2: $none2") }
   * }.suspended()
   * //sampleEnd
   * ```
   *
   * @see [take] for function that semantically blocks until a value becomes available.
   * @see [tryPeek] for a function that attempts to peek a value from the [Queue] without removing it.
   */
  fun tryTake(): Kind<F, Option<A>>

  /**
   * Peeks a value from the [Queue] or semantically blocks until a value becomes available.
   * In contrast to [take], [peek] does not remove the value from the [Queue].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   *
   * //sampleStart
   * suspend fun main(args: Array<String>): Unit = IO.fx {
   *   val queue = !Queue.unbounded<Int>()
   *   val (join, _) = !queue.peek().fork()
   *   !queue.offer(1) // Removing this offer makes, !join block forever.
   *   val res = !join // Join the blocking peek, after we offered a value
   *   val res2 = !queue.peek() // We can peek again since it doesn't remove the value
   *   !effect { println("res: $res, res2: $res2") }
   * }.suspended()
   * //sampleEnd
   * ```
   *
   * @see [take] for function that semantically blocks until a value becomes available and removes it from the [Queue].
   * @see [tryPeek] for a function that does not semantically blocks but returns immediately with an [Option].
   */
  fun peek(): Kind<F, A>

  /**
   * Tries to peek a value from the [Queue]. Returns immediately with either [None] or a value [Some].
   * In contrast to [tryTake], [tryPeek] does not remove the value from the [Queue].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   *
   * //sampleStart
   * suspend fun main(args: Array<String>): Unit = IO.fx {
   *   val queue = !Queue.unbounded<Int>()
   *   val none = !queue.tryPeek()
   *   !queue.offer(1)
   *   val one = !queue.tryPeek()
   *   val one2 = !queue.tryPeek()
   *   !effect { println("none: $none, one $one, one2: $one2") }
   * }.suspended()
   * //sampleEnd
   * ```
   *
   * @see [peek] for a function that semantically blocks until a value becomes available.
   * @see [tryTake] for a function that attempts to take a value from the [Queue] while removing it.
   */
  fun tryPeek(): Kind<F, Option<A>>

  /**
   * Immediately returns all available values in the [Queue], and empties the [Queue].
   * It returns an [emptyList] when no values are available.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   *
   * //sampleStart
   * suspend fun main(args: Array<String>): Unit = IO.fx {
   *   val queue = !Queue.unbounded<Int>()
   *   !queue.offerAll(1, 2, 3, 4)
   *   val values = !queue.takeAll()
   *   val empty = !queue.takeAll()
   *   !effect { println("values: $values, empty: $empty") }
   * }.suspended()
   * //sampleEnd
   * ```
   *
   * For a [BackpressureStrategy.Bounded], this also includes all blocking offers that are waiting to be added in the [Queue].
   *
   * @see [peekAll] for a function that doesn't remove the values from the [Queue].
   */
  fun takeAll(): Kind<F, List<A>>

  /**
   * Immediately returns all available values in the [Queue], without empty'ing the [Queue].
   * It returns an [emptyList] when no values are available.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   *
   * //sampleStart
   * suspend fun main(args: Array<String>): Unit = IO.fx {
   *   val queue = !Queue.unbounded<Int>()
   *   !queue.offerAll(1, 2, 3, 4)
   *   val values = !queue.peekAll()
   *   val values2 = !queue.peekAll()
   *   !effect { println("values: $values, values2: values2") }
   * }.suspended()
   * //sampleEnd
   * ```
   *
   * For a [BackpressureStrategy.Bounded], this also includes all blocking offers that are waiting to be added in the [Queue].
   *
   * @see [takeAll] for a function that also removes all values from the [Queue].
   */
  fun peekAll(): Kind<F, List<A>>
}

/**
 * [Enqueue] allows offering values to a [Queue], but doesn't allow taking values from the  [Queue].
 * You can use [Enqueue] to restrict certain functions or layers of your applications to only produce values.
 *
 * ```kotlin:ank:playground
  * import arrow.fx.*
  * import arrow.fx.extensions.*
  * import arrow.fx.typeclasses.*
  * import kotlin.coroutines.EmptyCoroutineContext
  *
  * //sampleStart
  * suspend fun main(args: Array<String>): Unit = IO.fx {
  *   fun produceInts(e: Enqueue<ForIO, Int>, max: Int): IOOf<Unit> =
  *     (0..max).parTraverse(EmptyCoroutineContext) { i ->
  *       IO.sleep(i * 10.milliseconds).followedBy(e.offer(i))
  *     }.void()
  *
  *   val queue = !Queue.unbounded<Int>()
  *   !produceInts(queue, 1000).fork()
  *   !IO.sleep(4.seconds)
  *   val res = !queue.takeAll()
  *   !effect { println(res) }
  * }.suspended()
  * //sampleEnd
 * ```
 *
 * @see Queue in the case your functions or layers are allowed to take and offer.
 * @see Dequeue in the case your functions or layers are only allowed to peek or take values.
 * */
interface Enqueue<F, A> {

  /**
   * Offers a value to the [Queue], and behaves differently depending on the [Queue.BackpressureStrategy].
   *
   *  - Semantically blocks until room available in [Queue], check [Queue.bounded] for an example.
   *
   *  - Returns immediately and slides values through the [Queue], check [Queue.sliding] for an example.
   *
   *  - Returns immediately and drops values from the [Queue], check [Queue.dropping] for an example.
   *
   *  - Returns immediately and always offers to the [Queue], check [Queue.unbounded] for an example.
   *
   *  @see [tryOffer] for a [Queue] that always returns immediately, and returns [true] if the value was succesfully put into the [Queue].
   */
  fun offer(a: A): Kind<F, Unit>

  /**
   * Tries to offer a value to the [Queue], it ignores the [Queue.BackpressureStrategy]
   * and returns false if the [Queue.BackpressureStrategy] does not have room for the value.
   *
   * Use [tryOffer] if you do not want to block or lose a value and return immediately.
   */
  fun tryOffer(a: A): Kind<F, Boolean>

  fun tryOfferAll(a: Iterable<A>): Kind<F, Boolean>

  fun tryOfferAll(vararg a: A): Kind<F, Boolean> =
    tryOfferAll(a.toList())

  fun offerAll(a: Iterable<A>): Kind<F, Unit>

  fun offerAll(vararg a: A): Kind<F, Unit> =
    offerAll(a.toList())
}

/**
 * Lightweight [Concurrent] [F] [Queue] for values of [A].
 *
 * A [Queue] can be used using 4 different back-pressure strategies:
 *
 *  - [bounded]: Offering to a bounded queue at capacity will cause the fiber making
 *   the call to be suspended until the queue has space to receive the offer value
 *
 *  - [dropping]: Offering to a dropping queue at capacity will cause the offered
 *   value to be discarded
 *
 *  - [sliding]: Offering to a sliding queue at capacity will cause the value at the
 *   front of the queue to be discarded to make room for the offered value
 *
 * - [unbounded]: An unbounded queue has no notion of capacity and is bound only by
 *   exhausting the memory limits of the runtime
 */
interface Queue<F, A> : QueueOf<F, A>, Dequeue<F, A>, Enqueue<F, A> {

  /**
   * Immediately returns the current size of values in the [Queue].
   * Can be a negative number when there are takers but no values available.
   */
  fun size(): Kind<F, Int>

  companion object {
    private fun <F> Concurrent<F>.ensureCapacity(capacity: Int): Kind<F, Int> =
      just(capacity).ensure(
        { IllegalArgumentException("Queue must have a capacity greater than 0") },
        { it > 0 }
      )

    /**
     * Create a [Queue] with [BackpressureStrategy.Bounded].
     *
     * Offering to a bounded queue at capacity will cause the fiber making
     * the call to be suspended until the queue has space to receive the offered value.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.fx
     * import arrow.fx.extensions.io.concurrent.concurrent
     *
     * suspend fun main(args: Array<String>): Unit = IO.fx {
     *   val capacity = 2
     *   val q = !Queue.bounded<Int>(capacity)
     *   !q.offer(42)
     *   !q.offer(43)
     *   !q.offer(44).fork() // <-- This `offer` exceeds the capacity and will be suspended
     *   val fortyTwo   = !q.take()
     *   val fortyThree = !q.take()
     *   val fortyFour  = !q.take()
     *   !effect { println(listOf(fortyTwo, fortyThree, fortyFour)) }
     * }.suspended()
     * ```
     */
    fun <F, A> bounded(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      ensureCapacity(capacity).map { n ->
        ConcurrentQueue<F, A>(Queue.BackpressureStrategy.Bounded(n), ConcurrentQueue.State.empty(), CF)
      }
    }

    /**
     * Create a [Queue] with [BackpressureStrategy.Sliding].
     *
     * Offering to a sliding queue at capacity will cause the value at the
     * front of the queue to be discarded to make room for the offered value.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.fx
     * import arrow.fx.extensions.io.concurrent.concurrent
     *
     * suspend fun main(args: Array<String>): Unit = IO.fx {
     *  val capacity = 2
     *  val q = !Queue.sliding<ForIO, Int>(capacity, IO.concurrent())
     *  !q.offer(42)
     *  !q.offer(43)
     *  !q.offer(44) // <-- This `offer` exceeds the capacity, causing the oldest value to be removed
     *  val fortyThree = !q.take()
     *  val fortyFour  = !q.take()
     *  !q.offer(45)
     *  val fortyFive  = !q.take()
     *  !effect { println(listOf(fortyThree, fortyFour, fortyFive)) }
     * }.suspended()
     * ```
     */
    fun <F, A> sliding(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      ensureCapacity(capacity).map { n ->
        ConcurrentQueue<F, A>(Queue.BackpressureStrategy.Sliding(n), ConcurrentQueue.State.empty(), CF)
      }
    }

    /**
     * Create a [Queue] with [BackpressureStrategy.Dropping].
     *
     * Offering to a dropping queue at capacity will cause the offered value to be discarded.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.fx
     * import arrow.fx.extensions.io.concurrent.concurrent
     *
     * suspend fun main(args: Array<String>): Unit = IO.fx {
     *   val capacity = 2
     *   val q = !Queue.dropping<ForIO, Int>(capacity, IO.concurrent())
     *   !q.offer(42)
     *   !q.offer(43)
     *   !q.offer(44) // <-- This `offer` exceeds the capacity and will be dropped immediately
     *   val fortyTwo   = !q.take()
     *   val fortyThree = !q.take()
     *   !q.offer(45)
     *   val fortyFive  = !q.take()
     *   !effect { println(listOf(fortyTwo, fortyThree, fortyFive)) }
     * }.suspended()
     * ```
     */
    fun <F, A> dropping(capacity: Int, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.run {
      ensureCapacity(capacity).map { n ->
        ConcurrentQueue<F, A>(Queue.BackpressureStrategy.Dropping(n), ConcurrentQueue.State.empty(), CF)
      }
    }

    /**
     * Create a [Queue] with [BackpressureStrategy.Unbounded].
     *
     * An unbounded queue has no notion of capacity and is bound only by exhausting the memory limits of the runtime.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.fx
     * import arrow.fx.extensions.io.concurrent.concurrent
     *
     * suspend fun main(args: Array<String>): Unit = IO.fx {
     *   val q = !Queue.unbounded<ForIO, Int>(IO.concurrent())
     *   !q.offer(42)
     *   // ...
     *   !q.offer(42000000)
     *   val res = !q.take()
     *   !effect { println(res) }
     * }.suspended()
     * ```
     */
    fun <F, A> unbounded(CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.later {
      ConcurrentQueue<F, A>(Queue.BackpressureStrategy.Unbounded, ConcurrentQueue.State.empty(), CF)
    }

    fun <F> factory(CF: Concurrent<F>): QueueFactory<F> =
      QueueFactory(CF)
  }

  /** Internal model that represent the Queue strategies **/
  sealed class BackpressureStrategy {
    data class Bounded(val capacity: Int) : BackpressureStrategy()
    data class Sliding(val capacity: Int) : BackpressureStrategy()
    data class Dropping(val capacity: Int) : BackpressureStrategy()
    object Unbounded : BackpressureStrategy()
  }
}

/**
 * Builds a [QueueFactory] for data type [F] without fixing the [Queue]'s [A] type or the [Queue.BackpressureStrategy].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.*
 * import arrow.fx.extensions.fx
 * import arrow.fx.extensions.io.concurrent.concurrent
 *
 * //sampleStart
 * suspend fun main(): Unit = IO.fx {
 *   val factory: QueueFactory<ForIO> = Queue.factory(IO.concurrent())
 *   val unbounded = !factory.unbounded<Int>()
 *   val bounded = !factory.bounded<String>(10)
 *   val sliding = !factory.sliding<Double>(4)
 *   val dropping = !factory.dropping<Float>(4)
 * }.suspended()
 * //sampleEnd
 * ```
 */
interface QueueFactory<F> {

  fun CF(): Concurrent<F>

  /**
   * Create a [Queue] with [BackpressureStrategy.Bounded].
   *
   * Offering to a bounded queue at capacity will cause the fiber making
   * the call to be suspended until the queue has space to receive the offer value.
   */
  fun <A> bounded(capacity: Int): Kind<F, Queue<F, A>> =
    Queue.bounded<F, A>(capacity, CF())

  /**
   * Create a [Queue] with [BackpressureStrategy.Sliding].
   *
   * Offering to a sliding queue at capacity will cause the value at the
   * front of the queue to be discarded to make room for the offered value
   */
  fun <A> sliding(capacity: Int): Kind<F, Queue<F, A>> =
    Queue.sliding(capacity, CF())

  /**
   * Create a [Queue] with [BackpressureStrategy.Dropping].
   *
   * Offering to a dropping queue at capacity will cause the offered value to be discarded.
   */
  fun <A> dropping(capacity: Int): Kind<F, Queue<F, A>> =
    Queue.dropping(capacity, CF())

  /**
   * Create a [Queue] with [BackpressureStrategy.Unbounded].
   *
   * An unbounded queue has no notion of capacity and is bound only by exhausting the memory limits of the runtime
   */
  fun <A> unbounded(): Kind<F, Queue<F, A>> =
    Queue.unbounded(CF())

  companion object {
    operator fun <F> invoke(CF: Concurrent<F>): QueueFactory<F> = object : QueueFactory<F> {
      override fun CF(): Concurrent<F> = CF
    }
  }
}
