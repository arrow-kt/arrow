package arrow.fx.coroutines.stream.concurrent

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.fx.coroutines.IQueue
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Pipe
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.Token

/**
 * Asynchronous Topic.
 *
 * Topic allows you to distribute `A` published by arbitrary number of publishers to arbitrary number of subscribers.
 *
 * Topic has built-in back-pressure support implemented as maximum bound (`maxQueued`) that a subscriber is allowed to enqueue.
 * Once that bound is hit, publishing may semantically block until the lagging subscriber consumes some of its queued elements.
 *
 * Additionally the subscriber has possibility to terminate whenever size of enqueued elements is over certain size
 * by using `subscribeSize`.
 */
@Deprecated("Stream is deprecated in favor of KotlinX Flow. Use Channel")
class Topic<A> internal constructor(
  private val pubSub: PubSub<A, Either<State<A>, IQueue<A>>, Either<Option<Token>, Pair<Token, Int>>>
) {

  /**
   * Publishes elements from source of `A` to this topic.
   * [Pipe] equivalent of `publish1`.
   */
  fun publish(): Pipe<A, Unit> =
    Pipe {
      it.effectMap(::publish1)
    }

  /**
   * Publishes one `A` to topic.
   *
   * This waits until `a` is published to all subscribers.
   * If any of the subscribers is over the `maxQueued` limit, this will wait to complete until that subscriber processes
   * enough of its elements such that `a` is enqueued.
   */
  suspend fun publish1(a: A): Unit =
    pubSub.publish(a)

  /**
   * Subscribes for `A` values that are published to this topic.
   *
   * Pulling on the returned stream opens a "subscription", which allows up to
   * `maxQueued` elements to be enqueued as a result of publication.
   *
   * The first element in the stream is always the last published `A` at the time
   * the stream is first pulled from, followed by each published `A` value from that
   * point forward.
   *
   * If at any point, the queue backing the subscription has `maxQueued` elements in it,
   * any further publications semantically block until elements are dequeued from the
   * subscription queue.
   *
   * @param maxQueued maximum number of elements to enqueue to the subscription
   * queue before blocking publishers
   */
  fun subscribe(maxQueued: Int): Stream<A> =
    subscriber(maxQueued)
      .flatMap { (_, s) ->
        s.flatMap { q ->
          Stream.chunk(Chunk.iterable(q))
        }
      }

  /**
   * Like [subscribe] but emits an approximate number of queued elements for this subscription
   * with each emitted `A` value.
   */
  fun subscribeSize(maxQueued: Int): Stream<Pair<A, Int>> =
    subscriber(maxQueued).flatMap { (selector, stream) ->
      stream.flatMap { q ->
        Stream.chunk(Chunk.iterable(q.mapIndexed { idx, a -> Pair(a, q.size - idx) }))
      }.effectMap { (a, remQ) ->
        when (val eith = pubSub.get(Either.Left(None))) {
          is Either.Left -> Pair(a, eith.a.subscribers[selector]?.size ?: remQ)
          is Either.Right -> Pair(a, -1) // Impossible
        }
      }
    }

  /**
   * Signal of current active subscribers.
   */
  fun subscribers(): Stream<Int> =
    Stream.bracket({ Token() }, { token -> pubSub.unsubscribe(Either.Left(Some(token))) })
      .flatMap { token ->
        pubSub.getStream(Either.Left(Some(token))).flatMap {
          when (it) {
            is Either.Left -> Stream.just(it.a.subscribers.size)
            is Either.Right -> Stream.empty() // Impossible
          }
        }
      }

  private fun subscriber(size: Int): Stream<Pair<Pair<Token, Int>, Stream<IQueue<A>>>> =
    Stream.bracket({
      Pair(Token(), size).also { selector ->
        pubSub.subscribe(Either.Right(selector))
      }
    }, { selector ->
      pubSub.unsubscribe(Either.Right(selector))
    }).map { selector ->
      Pair(selector, pubSub.getStream(Either.Right(selector)).flatMap {
        when (it) {
          is Either.Right -> Stream.just(it.b)
          is Either.Left -> Stream.empty() // Impossible
        }
      })
    }

  companion object {
    suspend operator fun <A> invoke(initial: A): Topic<A> {
      val pubSub: PubSub<A, Either<State<A>, IQueue<A>>, Either<Option<Token>, Pair<Token, Int>>> =
        PubSub.from(PubSub.Strategy.inspectable(boundedSubscribers(initial)))
      return Topic(pubSub)
    }
  }

  internal data class State<A>(
    val last: A,
    val subscribers: Map<Pair<Token, Int>, IQueue<A>>
  )
}

/**
 * Strategy for topic, where every subscriber can specify max size of queued elements.
 * If that subscription is exceeded any other `publish` to the topic will hold,
 * until such subscriber disappears, or consumes more elements.
 *
 * @param initial Initial value of the topic.
 */
private fun <A> boundedSubscribers(start: A): PubSub.Strategy<A, IQueue<A>, Topic.State<A>, Pair<Token, Int>> =
  object : PubSub.Strategy<A, IQueue<A>, Topic.State<A>, Pair<Token, Int>> {

    override val initial: Topic.State<A> =
      Topic.State(start, emptyMap())

    override fun accepts(i: A, state: Topic.State<A>): Boolean =
      state.subscribers.all { (s, q) -> q.size < s.second }

    override fun publish(i: A, state: Topic.State<A>): Topic.State<A> =
      Topic.State(i, state.subscribers.mapValues { (_, v) -> v.enqueue(i) })

    // Register empty queue
    fun regEmpty(selector: Pair<Token, Int>, state: Topic.State<A>): Topic.State<A> =
      state.copy(subscribers = state.subscribers + Pair(selector, IQueue.empty()))

    override fun get(selector: Pair<Token, Int>, state: Topic.State<A>): Pair<Topic.State<A>, Option<IQueue<A>>> =
      when (val r = state.subscribers[selector]) {
        null -> Pair(regEmpty(selector, state), Some(IQueue(state.last)))
        else -> if (r.isEmpty()) Pair(state, None)
        else Pair(regEmpty(selector, state), Some(r))
      }

    override fun empty(state: Topic.State<A>): Boolean =
      false

    override fun subscribe(selector: Pair<Token, Int>, state: Topic.State<A>): Pair<Topic.State<A>, Boolean> =
      Pair(state, true) // no subscribe necessary, as we always subscribe by first attempt to `get`

    override fun unsubscribe(selector: Pair<Token, Int>, state: Topic.State<A>): Topic.State<A> =
      state.copy(subscribers = state.subscribers - selector)
  }
