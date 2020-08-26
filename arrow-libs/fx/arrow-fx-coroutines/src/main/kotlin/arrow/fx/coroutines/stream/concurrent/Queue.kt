package arrow.fx.coroutines.stream.concurrent

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.fx.coroutines.IQueue
import arrow.fx.coroutines.andThen
import arrow.fx.coroutines.prependTo
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Pipe
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.terminateOnNone
import kotlin.math.min

/** Provides the ability to enqueue elements to a `Queue`. */
interface Enqueue<A> {

  /**
   * Enqueues one element to this `Queue`.
   * If the queue is `full` this waits until queue has space.
   *
   * This completes after `a`  has been successfully enqueued to this `Queue`
   */
  suspend fun enqueue1(a: A): Unit

  /**
   * Enqueues each element of the input stream to this queue by
   * calling `enqueue1` on each element.
   */
  fun enqueue(): Pipe<A, Unit> =
    Pipe { s -> s.effectMap { enqueue1(it) } }

  /**
   * Offers one element to this `Queue`.
   *
   * Evaluates to `false` if the queue is full, indicating the `a` was not queued up.
   * Evaluates to `true` if the `a` was queued up successfully.
   *
   * @param a `A` to enqueue
   */
  fun tryOffer1(a: A): Boolean
}

/** Provides the ability to dequeue individual elements from a `Queue`. */
interface Dequeue1<A> {

  /** Dequeues one `A` from this queue. Completes once one is ready. */
  suspend fun dequeue1(): A

  /**
   * Tries to dequeue a single element. Unlike `dequeue1`, this method does not semantically
   * block until a chunk is available - instead, `None` is returned immediately.
   */
  suspend fun tryDequeue1(): Option<A>
}

/** Provides the ability to dequeue chunks of elements from a `Queue` as streams. */
interface Dequeue<A> {

  /** Dequeues elements from the queue. */
  fun dequeue(): Stream<A> =
    dequeueChunk(Int.MAX_VALUE)

  /** Dequeues elements from the queue, ensuring elements are dequeued in chunks not exceeding `maxSize`. */
  fun dequeueChunk(maxSize: Int): Stream<A>

  /**
   * Provides a pipe that converts a stream of batch sizes in to a stream of elements by dequeuing
   * batches of the specified size.
   */
  fun dequeueBatch(): Pipe<Int, A>
}

/**
 * A queue of elements. Operations are all nonblocking in their
 * implementations, but may be 'semantically' blocking. For instance,
 * a queue may have a bound on its size, in which case enqueuing may
 * block (be delayed asynchronously) until there is an offsetting dequeue.
 */
interface Queue<A> : Enqueue<A>, Dequeue1<A>, Dequeue<A> {

  /** Dequeues one `Chunk[A]` with no more than `maxSize` elements. Completes once one is ready. */
  suspend fun dequeueChunk1(maxSize: Int): Chunk<A>

  /**
   * Tries to dequeue a single chunk of no more than `max size` elements.
   * Unlike `dequeueChunk1`, this method does not semantically block until a chunk is available -
   * instead, `None` is returned immediately.
   */
  suspend fun tryDequeueChunk1(maxSize: Int): Option<Chunk<A>>

  /**
   * Returns an alternate view of this `Queue` where its elements are of type `B`,
   * given two functions, `(A) -> B` and `(B) -> A`.
   */
  fun <B> imap(f: (A) -> B, g: (B) -> A): Queue<B> =
    object : Queue<B> {
      override suspend fun enqueue1(a: B) = enqueue1(g(a))
      override fun tryOffer1(a: B): Boolean = tryOffer1(g(a))
      override suspend fun dequeue1(): B = f(this@Queue.dequeue1())
      override suspend fun tryDequeue1(): Option<B> = this@Queue.tryDequeue1().map(f)
      override suspend fun dequeueChunk1(maxSize: Int): Chunk<B> = this@Queue.dequeueChunk1(maxSize).map(f)
      override suspend fun tryDequeueChunk1(maxSize: Int): Option<Chunk<B>> =
        this@Queue.tryDequeueChunk1(maxSize).map { it.map(f) }

      override fun dequeueChunk(maxSize: Int): Stream<B> = this@Queue.dequeueChunk(maxSize).map(f)
      override fun dequeueBatch(): Pipe<Int, B> = this@Queue.dequeueBatch().andThen { it.map(f) }
    }

  companion object {

    /** Creates a queue from the supplied strategy. */
    private fun <S, A> fromStrategy(strategy: PubSub.Strategy<A, Chunk<A>, S, Int>): Queue<A> {
      val pubSub = PubSub.unsafe(strategy)
      return DefaultQueue(pubSub)
    }

    /** Creates a queue from the supplied strategy. */
    private fun <S, A> fromStrategyNoneTerminated(strategy: PubSub.Strategy<Option<A>, Option<Chunk<A>>, S, Int>): NoneTerminatedQueue<A> {
      val pubSub = PubSub.unsafe(strategy)
      return DefaultNoneTerminatedQueue(pubSub)
    }

    /** Creates a FIFO queue with no size bound. */
    suspend fun <A> unbounded(): Queue<A> =
      fromStrategy(Strategy.fifo())

    fun <A> unsafeUnbounded(): Queue<A> =
      fromStrategy(Strategy.fifo())

    /** Creates an unbounded FIFO queue that distributed always at max `fairSize` elements to any subscriber. */
    suspend fun <A> fairUnbounded(fairSize: Int): Queue<A> =
      fromStrategy(Strategy.fifo<A>().transformSelector { size, _ -> min(size, fairSize) })

    fun <A> unsafeFairUnbounded(fairSize: Int): Queue<A> =
      fromStrategy(Strategy.fifo<A>().transformSelector { size, _ -> min(size, fairSize) })

    /**
     * Creates a FIFO queue with the specified size bound.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.coroutines.*
     * import arrow.fx.coroutines.stream.*
     * import arrow.fx.coroutines.stream.concurrent.*
     *
     * suspend fun main(): Unit {
     *   val q = Queue.bounded<Int>(10)
     *   Stream(
     *     Stream.range(0..100)
     *       .through(q.enqueue())
     *       .void(),
     *     q.dequeue()
     *   ).parJoinUnbounded()
     *     .take(100)
     *     .toList().let(::println) // [0, 1, 2, .., 99]
     *
     *   val alwaysEmpty = Queue.bounded<Int>(0)
     *   ForkConnected { alwaysEmpty.dequeue1() }
     *   alwaysEmpty.tryOffer1(1).let(::println) // false
     * }
     * ```
     *
     * A size <= 0, will not allow any elements to pass through the [Queue],
     * in that case it will always return `false` for `tryOffer1`.
     *
     * @see Queue.synchronous If you need a [Queue] that suspends until both a [dequeue] & [enqueue] happen, or handshake.
     */
    suspend fun <A> bounded(maxSize: Int): Queue<A> =
      fromStrategy(Strategy.boundedFifo(maxSize))

    fun <A> unsafeBounded(maxSize: Int): Queue<A> =
      fromStrategy(Strategy.boundedFifo(maxSize))

    /** Creates a queue which stores the last `maxSize` enqueued elements and which never blocks on enqueue. */
    suspend fun <A> sliding(maxSize: Int): Queue<A> =
      fromStrategy(Strategy.sliding(maxSize))

    fun <A> unsafeSliding(maxSize: Int): Queue<A> =
      fromStrategy(Strategy.sliding(maxSize))

    /** Creates a queue which stores the first `maxSize` enqueued elements and which never blocks on enqueue. */
    suspend fun <A> dropping(maxSize: Int): Queue<A> =
      fromStrategy(Strategy.dropping(maxSize))

    fun <A> unsafeDropping(maxSize: Int): Queue<A> =
      fromStrategy(Strategy.dropping(maxSize))

    /** Created a bounded queue that distributed always at max `fairSize` elements to any subscriber. */
    suspend fun <A> fairBounded(maxSize: Int, fairSize: Int): Queue<A> =
      fromStrategy(Strategy.boundedFifo<A>(maxSize).transformSelector { size, _ -> min(size, fairSize) })

    fun <A> unsafeFairBounded(maxSize: Int, fairSize: Int): Queue<A> =
      fromStrategy(Strategy.boundedFifo<A>(maxSize).transformSelector { size, _ -> min(size, fairSize) })

    /**
     * Creates a [Queue] in which each [enqueue] operation must wait for a corresponding [dequeue] operation, and vice versa.
     * In other words, [dequeue] and [enqueue] need to shake hands, or meet, before the value is successfully passed along.
     * Works like functional suspending version [java.util.concurrent.SynchronousQueue].
     */
    suspend fun <A> synchronous(): Queue<A> =
      fromStrategy(Strategy.synchronous())

    fun <A> unsafeSynchronous(): Queue<A> =
      fromStrategy(Strategy.synchronous())

    /** Like [synchronous], except that any enqueue of `None` will never block and cancels any dequeue operation. */
    suspend fun <A> synchronousNoneTerminated(): NoneTerminatedQueue<A> {
      val strategy = Strategy.synchronous<A>()
      val pubSub = PubSub.Strategy.closeNowOption(strategy)
      return fromStrategyNoneTerminated(pubSub)
    }

    fun <A> unsafeSynchronousNoneTerminated(): NoneTerminatedQueue<A> {
      val strategy = Strategy.synchronous<A>()
      val pubSub = PubSub.Strategy.closeNowOption(strategy)
      return fromStrategyNoneTerminated(pubSub)
    }
  }
}

interface NoneTerminatedQueue<A> : Enqueue<Option<A>>, Dequeue1<Option<A>>, Dequeue<A> {

  /** Dequeues one `Chunk[A]` with no more than `maxSize` elements. Completes once one is ready. */
  suspend fun dequeueChunk1(maxSize: Int): Option<Chunk<A>>

  /**
   * Tries to dequeue a single chunk of no more than `max size` elements.
   * Unlike `dequeueChunk1`, this method does not semantically block until a chunk is available -
   * instead, `None` is returned immediately.
   */
  suspend fun tryDequeueChunk1(maxSize: Int): Option<Chunk<A>>
}

private fun <A> Chunk<A>.firstUnsafe(): A =
  if (size() == 1) this[0] else throw Throwable("Expected chunk of size 1. got $this")

internal object Strategy {

  /** Unbounded fifo strategy. */
  fun <A> boundedFifo(maxSize: Int): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    PubSub.Strategy.bounded(maxSize, fifo()) { it.size }

  /** Unbounded lifo strategy. */
  fun <A> boundedLifo(maxSize: Int): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    PubSub.Strategy.bounded(maxSize, lifo()) { it.size }

  /** Strategy for sliding, which stores the last `maxSize` enqueued elements and never blocks on enqueue. */
  fun <A> sliding(maxSize: Int): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    unbounded { q: IQueue<A>, a ->
      if (q.size <= maxSize) q.enqueue(a)
      else q.drop(1).enqueue(a)
    }

  /** Strategy for dropping, which stores the first `maxSize` enqueued elements and never blocks on enqueue. */
  fun <A> dropping(maxSize: Int): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    unbounded { q: IQueue<A>, a ->
      if (q.size <= maxSize) q.enqueue(a) else q
    }

  /** Unbounded lifo strategy. */
  fun <A> lifo(): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    unbounded { q, a -> a prependTo q }

  /** Unbounded fifo strategy. */
  fun <A> fifo(): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    unbounded(IQueue<A>::enqueue)

  /**
   * Strategy that allows at most a single element to be published.
   * Before the `A` is published successfully, at least one subscriber must be ready to consume.
   */
  fun <A> synchronous(): PubSub.Strategy<A, Chunk<A>, Pair<Boolean, Option<A>>, Int> =
    object : PubSub.Strategy<A, Chunk<A>, Pair<Boolean, Option<A>>, Int> {

      override val initial: Pair<Boolean, Option<A>> =
        Pair(false, None)

      override fun accepts(i: A, state: Pair<Boolean, Option<A>>): Boolean =
        state.first && state.second.isEmpty()

      override fun publish(i: A, state: Pair<Boolean, Option<A>>): Pair<Boolean, Option<A>> =
        Pair(state.first, Some(i))

      override fun get(
        selector: Int,
        state: Pair<Boolean, Option<A>>
      ): Pair<Pair<Boolean, Option<A>>, Option<Chunk<A>>> =
        when (val opt = state.second) {
          None -> Pair(Pair(true, None), None)
          is Some -> Pair(Pair(false, None), Some(Chunk.just(opt.t)))
        }

      override fun empty(state: Pair<Boolean, Option<A>>): Boolean =
        state.second.isEmpty()

      override fun subscribe(
        selector: Int,
        state: Pair<Boolean, Option<A>>
      ): Pair<Pair<Boolean, Option<A>>, Boolean> =
        Pair(state, false)

      override fun unsubscribe(selector: Int, state: Pair<Boolean, Option<A>>): Pair<Boolean, Option<A>> =
        state
    }

  /**
   * Creates unbounded queue strategy for `A` with configurable append function.
   *
   * @param append function used to append new elements to the queue
   */
  fun <A> unbounded(append: (IQueue<A>, A) -> IQueue<A>): PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> =
    object : PubSub.Strategy<A, Chunk<A>, IQueue<A>, Int> {

      override val initial: IQueue<A> = IQueue.empty()

      override fun publish(i: A, state: IQueue<A>): IQueue<A> =
        append(state, i)

      override fun accepts(i: A, state: IQueue<A>): Boolean =
        true

      override fun empty(state: IQueue<A>): Boolean =
        state.isEmpty()

      override fun get(selector: Int, state: IQueue<A>): Pair<IQueue<A>, Option<Chunk<A>>> =
        if (state.isEmpty()) Pair(state, None)
        else {
          val (out, rem) = Chunk.Queue.queueFirstN(selector, state)
          Pair(rem, Some(out))
        }

      override fun subscribe(selector: Int, state: IQueue<A>): Pair<IQueue<A>, Boolean> =
        Pair(state, false)

      override fun unsubscribe(selector: Int, state: IQueue<A>): IQueue<A> =
        state
    }
}

internal class DefaultQueue<A>(private val pubSub: PubSub<A, Chunk<A>, Int>) : Queue<A> {
  override suspend fun enqueue1(a: A) =
    pubSub.publish(a)

  override fun tryOffer1(a: A): Boolean =
    pubSub.tryPublish(a)

  override suspend fun dequeue1(): A =
    pubSub.get(1).firstUnsafe()

  override suspend fun tryDequeue1(): Option<A> =
    pubSub.tryGet(1).fold({ None }) { Some(it.firstUnsafe()) }

  override suspend fun dequeueChunk1(maxSize: Int): Chunk<A> =
    pubSub.get(maxSize)

  override suspend fun tryDequeueChunk1(maxSize: Int): Option<Chunk<A>> =
    pubSub.tryGet(maxSize)

  override fun dequeueChunk(maxSize: Int): Stream<A> =
    pubSub.getStream(maxSize)
      .flatMap(Stream.Companion::chunk)

  override fun dequeueBatch(): Pipe<Int, A> =
    Pipe {
      it.flatMap { size ->
        Stream.effectUnChunk { pubSub.get(size) }
      }
    }
}

internal class DefaultNoneTerminatedQueue<A>(
  private val pubSub: PubSub<Option<A>, Option<Chunk<A>>, Int>
) : NoneTerminatedQueue<A> {
  override suspend fun enqueue1(a: Option<A>) =
    pubSub.publish(a)

  override fun tryOffer1(a: Option<A>): Boolean =
    pubSub.tryPublish(a)

  override suspend fun dequeue1(): Option<A> =
    pubSub.get(1).map { it.firstUnsafe() }

  override suspend fun tryDequeue1(): Option<Option<A>> =
    pubSub.tryGet(1).map { opt ->
      opt.map { it.firstUnsafe() }
    }

  override suspend fun dequeueChunk1(maxSize: Int): Option<Chunk<A>> =
    pubSub.get(maxSize)

  override suspend fun tryDequeueChunk1(maxSize: Int): Option<Chunk<A>> =
    pubSub.tryGet(maxSize).map { opt ->
      opt.fold({ Chunk.empty<A>() }, ::identity)
    }

  override fun dequeueChunk(maxSize: Int): Stream<A> =
    pubSub.getStream(maxSize)
      .terminateOnNone()
      .flatMap(Stream.Companion::chunk)

  override fun dequeueBatch(): Pipe<Int, A> =
    Pipe {
      it.effectMap { size ->
        pubSub.get(size)
      }.terminateOnNone()
        .flatMap(Stream.Companion::chunk)
    }
}
