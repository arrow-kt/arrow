package arrow.fx.coroutines.stream.concurrent

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.IQueue
import arrow.fx.coroutines.Token
import arrow.fx.coroutines.UnsafePromise
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Stream
import arrow.typeclasses.Eq
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlinx.atomicfu.update

internal interface Publish<A> {

  /**
   * Publishes one element.
   * This completes after element was successfully published.
   */
  suspend fun publish(a: A): Unit

  /**
   * Tries to publish one element.
   *
   * Evaluates to `false` if element was not published.
   * Evaluates to `true` if element was published successfully.
   */
  fun tryPublish(a: A): Boolean
}

internal interface Subscribe<A, Selector> {

  /**
   * Gets elements satisfying the `selector`, yielding when such an element is available.
   *
   * @param selector selector describing which `A` to receive
   */
  suspend fun get(selector: Selector): A

  /**
   * A variant of `get`, that instead or returning one element will return multiple elements
   * in form of stream.
   *
   * @param selector selector describing which `A` to receive
   * @return
   */
  fun getStream(selector: Selector): Stream<A>

  /**
   * Like `get`, but instead of semantically blocking for a matching element, returns immediately
   * with `None` if such an element is not available.
   *
   * @param selector selector describing which `A` to receive
   */
  suspend fun tryGet(selector: Selector): Option<A>

  /**
   * Creates a subscription for the supplied selector.
   * If the subscription is not supported or not successful, this yields to false.
   * @param selector selector describing which `A` to receive
   */
  suspend fun subscribe(selector: Selector): Boolean

  /**
   * Cancels a subscription previously registered with [subscribe].
   * Must be invoked if the subscriber will no longer consume elements.
   *
   * @param selector selector to unsubscribe
   */
  suspend fun unsubscribe(selector: Selector): Unit
}

internal interface PubSub<I, O, Selector> : Publish<I>, Subscribe<O, Selector> {

  data class Publisher<A>(val token: Token, val i: A, val signal: UnsafePromise<Unit>) {
    fun complete(): Unit {
        signal.complete(Result.success(Unit))
    }
  }

  data class Subscriber<A, Selector>(val token: Token, val selector: Selector, val signal: UnsafePromise<A>) {
    fun complete(a: A): Unit {
        signal.complete(Result.success(a))
    }
  }

  data class PubSubState<I, O, QS, Selector>(
    val queue: QS,
    val publishers: IQueue<Publisher<I>>,
    val subscribers: IQueue<Subscriber<O, Selector>>
  )

  companion object {

    suspend fun <I, O, QS, S> from(strategy: Strategy<I, O, QS, S>): PubSub<I, O, S> =
      unsafe(strategy)

    fun <I, O, QS, S> unsafe(strategy: Strategy<I, O, QS, S>): PubSub<I, O, S> =
      DefaultPubSub(strategy)
  }

  /**
   * Describes a the behavior of a `PubSub`.
   *
   * @tparam I the type of element that may be published
   * @tparam O the type of element that may be subscribed for
   * @tparam S the type of the internal state of the strategy
   * @tparam Selector the type of the output element selector
   */
  interface Strategy<I, O, S, Selector> {

    /** Initial state of this strategy. **/
    val initial: S

    /**
     * Verifies if `I` can be accepted.
     *
     * If this yields to true, then the pubsub can accept this element, and interpreter is free to invoke `publish`.
     * If this yields to false, then interpreter holds the publisher, until there is at least one  `get`
     * (either successsful or not) in which case this is consulted again.
     *
     * @param i `I` to publish
     */
    fun accepts(i: I, state: S): Boolean

    /**
     * Publishes `I`. This must always succeed.
     *
     * Interpreter must only invoke this when `accepts` yields to true.
     *
     * @param i `I` to publish
     */
    fun publish(i: I, state: S): S

    /**
     * Gets `O`, selected by `selector`.
     *
     * Yields to `None`, if subscriber cannot be satisfied, causing the subscriber to hold, until next successful `publish`
     * Yields to `Some((s,o))` if the subscriber may be satisfied.
     *
     * @param selector specifies which `O` this `get` is interested in. In case of a subscription
     *                 based strategy, the `Selector` shall hold the identity of the subscriber.
     */
    fun get(selector: Selector, state: S): Pair<S, Option<O>>

    /**
     * Yields to true if there are no elements to `get`.
     */
    fun empty(state: S): Boolean

    /**
     * Consulted by interpreter to subscribe the given selector.
     * A subscriptions manages context/state across multiple `get` requests.
     * Yields to false if the subscription cannot be satisfied.
     *
     * @param selector selector that shall be used with mulitple subsequent `get` operations
     */
    fun subscribe(selector: Selector, state: S): Pair<S, Boolean>

    /**
     * When strategy supports long-term subscriptions, this is used by interpreter
     * to cancel a previous subscription, indicating the subscriber is no longer interested
     * in getting more data.
     *
     * @param selector selector whose selection shall be canceled
     */
    fun unsubscribe(selector: Selector, state: S): S

    /** Transforms selector to selector of this state by applying the `f` to `Sel2` and state of this strategy. **/
    fun <S2> transformSelector(f: (S2, S) -> Selector): Strategy<I, O, S, S2> =
      object : Strategy<I, O, S, S2> {
        override val initial: S = this@Strategy.initial

        override fun accepts(i: I, state: S): Boolean =
          this@Strategy.accepts(i, state)

        override fun publish(i: I, state: S): S =
          this@Strategy.publish(i, state)

        override fun get(selector: S2, state: S): Pair<S, Option<O>> =
          this@Strategy.get(f(selector, state), state)

        override fun empty(state: S): Boolean =
          this@Strategy.empty(state)

        override fun subscribe(selector: S2, state: S): Pair<S, Boolean> =
          this@Strategy.subscribe(f(selector, state), state)

        override fun unsubscribe(selector: S2, state: S): S =
          this@Strategy.unsubscribe(f(selector, state), state)
      }

    companion object {

      /**
       * Creates bounded strategy, that won't accept elements if size produced by `f` is >= `maxSize`.
       *
       * @param maxSize maximum size of enqueued `A` before this is full
       * @param f function to extract current size of `S`
       */
      fun <A, S> bounded(
        maxSize: Int,
        strategy: Strategy<A, Chunk<A>, S, Int>,
        f: (S) -> Int
      ): Strategy<A, Chunk<A>, S, Int> =
        object : Strategy<A, Chunk<A>, S, Int> {

          override val initial: S = strategy.initial

          override fun publish(a: A, state: S): S =
            strategy.publish(a, state)

          override fun accepts(i: A, state: S): Boolean =
            f(state) < maxSize

          override fun empty(state: S): Boolean =
            strategy.empty(state)

          override fun get(selector: Int, state: S): Pair<S, Option<Chunk<A>>> =
            strategy.get(selector, state)

          override fun subscribe(selector: Int, state: S): Pair<S, Boolean> =
            strategy.subscribe(selector, state)

          override fun unsubscribe(selector: Int, state: S): S =
            strategy.unsubscribe(selector, state)
        }

      /**
       * Adapts a strategy to one that supports closing.
       *
       * Closeable PubSub are closed by publishing `None` instead of Some(a).
       * The close takes precedence over any elements.
       *
       * When the PubSub is closed, then
       *  - every `publish` is successful and completes immediately.
       *  - every `get` completes immediately with None instead of `O`.
       */
      fun <I, O, S, Sel> closeNowOption(strategy: Strategy<I, O, S, Sel>): Strategy<Option<I>, Option<O>, Option<S>, Sel> =
        object : Strategy<Option<I>, Option<O>, Option<S>, Sel> {
          override val initial: Option<S> = Some(strategy.initial)

          override fun accepts(i: Option<I>, state: Option<S>): Boolean =
            i.forall { el ->
              state.exists { s ->
                strategy.accepts(el, s)
              }
            }

          override fun publish(i: Option<I>, state: Option<S>): Option<S> =
            i.flatMap { el -> state.map { s -> strategy.publish(el, s) } }

          override fun get(selector: Sel, state: Option<S>): Pair<Option<S>, Option<Option<O>>> =
            when (state) {
              None -> Pair(None, Some(None))
              is Some -> {
                val (s1, result) = strategy.get(selector, state.t)
                Pair(Some(s1), result.map { Some(it) })
              }
            }

          override fun empty(state: Option<S>): Boolean =
            state.forall(strategy::empty)

          override fun subscribe(selector: Sel, state: Option<S>): Pair<Option<S>, Boolean> =
            when (state) {
              None -> Pair(None, false)
              is Some -> {
                val (s1, success) = strategy.subscribe(selector, state.t)
                Pair(Some(s1), success)
              }
            }

          override fun unsubscribe(selector: Sel, state: Option<S>): Option<S> =
            state.map { s ->
              strategy.unsubscribe(selector, s)
            }
        }

      /**
       * Adapts a strategy to one that supports closing.
       *
       * Closeable PubSub are closed by publishing `None` instead of Some(a).
       * The close takes precedence over any elements.
       *
       * When the PubSub is closed, then
       *  - every `publish` is successful and completes immediately.
       *  - every `get` completes immediately with None instead of `O`.
       */
      fun <I, O, S, Sel> closeNowOnNull(strategy: Strategy<I, O, S, Sel>): Strategy<I?, O, S?, Sel> =
        object : Strategy<I?, O, S?, Sel> {
          override val initial: S = strategy.initial

          override fun accepts(i: I?, state: S?): Boolean =
            i?.let { ii ->
              state?.let { s ->
                strategy.accepts(ii, s)
              }
            } ?: false

          override fun publish(i: I?, state: S?): S? =
            i?.let { el ->
              state?.let { s -> strategy.publish(el, s) }
            }

          override fun get(selector: Sel, state: S?): Pair<S?, Option<O>> =
            when (state) {
              null -> Pair(null, None)
              else -> {
                val (s1, result) = strategy.get(selector, state)
                Pair(s1, result.map { it })
              }
            }

          override fun empty(state: S?): Boolean =
            state?.let { strategy.empty(it) } ?: true

          override fun subscribe(selector: Sel, state: S?): Pair<S?, Boolean> =
            when (state) {
              null -> Pair(null, false)
              else -> {
                val (s1, success) = strategy.subscribe(selector, state)
                Pair(s1, success)
              }
            }

          override fun unsubscribe(selector: Sel, state: S?): S? =
            state?.let {
              strategy.unsubscribe(selector, it)
            }
        }

      /**
       * Like [closeNowOption] but instead of terminating immediately,
       * the pubsub will terminate when all elements are consumed.
       *
       * When the PubSub is closed, but not all elements yet consumed,
       * any publish operation will complete immediately.
       */
      fun <I, O, S, Sel> closeDrainFirst(strategy: Strategy<I, O, S, Sel>): Strategy<Option<I>, Option<O>, Pair<Boolean, S>, Sel> =
        object : Strategy<Option<I>, Option<O>, Pair<Boolean, S>, Sel> {
          override val initial: Pair<Boolean, S> = Pair(true, strategy.initial)

          override fun accepts(i: Option<I>, state: Pair<Boolean, S>): Boolean =
            when (i) {
              None -> true
              is Some -> !state.first || strategy.accepts(i.t, state.second)
            }

          override fun publish(i: Option<I>, state: Pair<Boolean, S>): Pair<Boolean, S> =
            when (i) {
              None -> Pair(false, state.second)
              is Some -> if (state.first) Pair(true, strategy.publish(i.t, state.second)) else state
            }

          override fun get(selector: Sel, state: Pair<Boolean, S>): Pair<Pair<Boolean, S>, Option<Option<O>>> =
            if (state.first) {
              val (s1, result) = strategy.get(selector, state.second)
              Pair(Pair(true, s1), result.map { Some(it) })
            } else {
              if (strategy.empty(state.second)) Pair(state, Some(None))
              else {
                val (s1, result) = strategy.get(selector, state.second)
                Pair(Pair(false, s1), result.map { Some(it) })
              }
            }

          override fun empty(state: Pair<Boolean, S>): Boolean =
            strategy.empty(state.second) && state.first

          override fun subscribe(selector: Sel, state: Pair<Boolean, S>): Pair<Pair<Boolean, S>, Boolean> =
            if (state.first) {
              val (s, success) = strategy.subscribe(selector, state.second)
              Pair(Pair(true, s), success)
            } else {
              Pair(state, false)
            }

          override fun unsubscribe(selector: Sel, state: Pair<Boolean, S>): Pair<Boolean, S> =
            Pair(state.first, strategy.unsubscribe(selector, state.second))
        }

      /**
       * State of the discrete strategy.
       *
       * Allows consumption of `A` values that may arrive out of order,
       * however signals the discrete values in order, as they have been received.
       *
       * @param last Last value known
       * @param lastStamp Stamp of the last value. Next value will be accepted, if the incoming `A` will have previous
       *                     stamp set to this value. In that case, the stamp is swapped to new stamp of incoming value and `last` is updated.
       * @param outOfOrder If there are arriving any `A` which has out of order stamp, this keeps these from resolving them laters
       * @param seen A set of subscribers, that have seen the `last`
       */
      data class DiscreteState<A>(
        val last: A,
        val lastStamp: Long,
        val outOfOrder: Map<Long, Pair<Long, A>>,
        val seen: Set<Token>
      )

      /**
       * Strategy providing possibility for a discrete `get` in correct order.
       *
       * Elements incoming as tuples of their timestamp and value. Also, each publish contains
       * timestamp of previous value, that should be replaced by new value.
       *
       * @param start initial value `A`
       * @param stamp initial stamp of `A`
       */
      fun <A> discrete(
        stamp: Long,
        start: A
      ): Strategy<Pair<Long, Pair<Long, A>>, A, DiscreteState<A>, Option<Token>> =
        object : Strategy<Pair<Long, Pair<Long, A>>, A, DiscreteState<A>, Option<Token>> {
          override val initial: DiscreteState<A> =
            DiscreteState(start, stamp, emptyMap(), emptySet())

          override fun accepts(i: Pair<Long, Pair<Long, A>>, state: DiscreteState<A>): Boolean =
            true

          private tailrec fun publishLoop(
            stamp: Long,
            curr: A,
            outOfOrder: Map<Long, Pair<Long, A>>
          ): DiscreteState<A> =
            when (val res = outOfOrder[stamp]) {
              null -> DiscreteState(curr, stamp, outOfOrder, emptySet())
              else -> publishLoop(res.first, res.second, outOfOrder - stamp)
            }

          override fun publish(i: Pair<Long, Pair<Long, A>>, state: DiscreteState<A>): DiscreteState<A> {
            val (prev, stampedA) = i
            return if (prev != state.lastStamp) state.copy(outOfOrder = state.outOfOrder + i)
            else publishLoop(stampedA.first, stampedA.second, state.outOfOrder)
          }

          override fun get(selector: Option<Token>, state: DiscreteState<A>): Pair<DiscreteState<A>, Option<A>> =
            when (selector) {
              None -> Pair(state, Some(state.last))
              is Some ->
                if (state.seen.contains(selector.t)) Pair(state, None)
                else Pair(state.copy(seen = state.seen + selector.t), Some(state.last))
            }

          override fun empty(state: DiscreteState<A>): Boolean =
            false

          override fun subscribe(selector: Option<Token>, state: DiscreteState<A>): Pair<DiscreteState<A>, Boolean> =
            when (selector) {
              None -> Pair(state, false)
              is Some -> Pair(state, true)
            }

          override fun unsubscribe(selector: Option<Token>, state: DiscreteState<A>): DiscreteState<A> =
            when (selector) {
              None -> state
              is Some -> state.copy(seen = state.seen - selector.t)
            }
        }

      /**
       * Allows to enhance the supplied strategy by ability to inspect the state.
       * If the `S` is same as previous state (by applying the supplied `Eq` then
       * `get` will not be signalled, if invoked with `Left(Some(token))` - subscription based
       * subscriber.
       *
       * @return
       */
      fun <I, O, S, Sel> inspectable(
        strategy: Strategy<I, O, S, Sel>,
        SEQ: Eq<S> = Eq.any()
      ): Strategy<I, Either<S, O>, InspectableState<S>, Either<Option<Token>, Sel>> =
        object : Strategy<I, Either<S, O>, InspectableState<S>, Either<Option<Token>, Sel>>, Eq<S> by SEQ {

          override val initial: InspectableState<S> =
            InspectableState(strategy.initial, emptySet())

          override fun accepts(i: I, state: InspectableState<S>): Boolean =
            strategy.accepts(i, state.qs)

          override fun publish(i: I, state: InspectableState<S>): InspectableState<S> {
            val qs1 = strategy.publish(i, state.qs)
            return if (state.qs.eqv(qs1)) state.copy(qs = qs1)
            else InspectableState(qs1, emptySet())
          }

          override fun get(
            selector: Either<Option<Token>, Sel>,
            state: InspectableState<S>
          ): Pair<InspectableState<S>, Option<Either<S, O>>> =
            when (selector) {
              is Either.Left -> when (val optToken = selector.a) {
                None -> Pair(state, Some(Either.Left(state.qs)))
                is Some ->
                  if (state.inspected.contains(optToken.t)) Pair(state, None)
                  else Pair(state.copy(inspected = state.inspected + optToken.t), Some(Either.Left(state.qs)))
              }
              is Either.Right -> {
                val (s, r) = strategy.get(selector.b, state.qs)
                val tokens: Set<Token> = if (state.qs.eqv(s)) state.inspected else emptySet()
                Pair(InspectableState(s, tokens), r.map { Either.Right(it) })
              }
            }

          override fun empty(state: InspectableState<S>): Boolean =
            strategy.empty(state.qs)

          override fun subscribe(
            selector: Either<Option<Token>, Sel>,
            state: InspectableState<S>
          ): Pair<InspectableState<S>, Boolean> =
            when (selector) {
              is Either.Left -> when (selector.a) {
                None -> Pair(state, false)
                is Some -> Pair(state, true)
              }
              is Either.Right -> {
                val (s, result) = strategy.subscribe(selector.b, state.qs)
                Pair(state.copy(qs = s), result)
              }
            }

          override fun unsubscribe(
            selector: Either<Option<Token>, Sel>,
            state: InspectableState<S>
          ): InspectableState<S> =
            when (selector) {
              is Either.Left -> when (val t = selector.a) {
                None -> state
                is Some -> state.copy(inspected = state.inspected - t.t)
              }
              is Either.Right -> {
                val qs1 = strategy.unsubscribe(selector.b, state.qs)
                val tokens: Set<Token> = if (state.qs.eqv(qs1)) state.inspected else emptySet()
                InspectableState(qs1, tokens)
              }
            }
        }
    }
  }

  /**
   * State representation for inspectable strategy that
   * keeps track of strategy state and keeps track of all subscribers that have seen the last known state.
   *
   * @param qs State of the strategy to be inspected
   * @param inspected List of subscribers that have seen the `qs` already
   */
  data class InspectableState<S>(val qs: S, val inspected: Set<Token>)
}

internal class DefaultPubSub<I, O, QS, S>(private val strategy: PubSub.Strategy<I, O, QS, S>) : PubSub<I, O, S> {

  val initial = PubSub.PubSubState<I, O, QS, S>(strategy.initial, IQueue.empty(), IQueue.empty())

  val state = atomic(initial)

  fun <X> update(f: (PubSub.PubSubState<I, O, QS, S>) -> Pair<PubSub.PubSubState<I, O, QS, S>, X>): X =
    state.modify { ps ->
      val (ps1, result) = f(ps)
      val (ps2, action) = loop(ps1) { Unit }
      Pair(ps2, { action.invoke(); result })
    }.invoke()

  suspend fun <X> modify(f: (PubSub.PubSubState<I, O, QS, S>) -> Pair<PubSub.PubSubState<I, O, QS, S>, suspend () -> X>): X =
    state.modify { ps ->
      val (ps1, result) = f(ps)
      val (ps2, action) = loop(ps1) { Unit }
      Pair(ps2, suspend { action.invoke(); result.invoke() })
    }.invoke()

  private fun clearPublisherOnErrorOrCancel(token: Token, exitCase: ExitCase): Unit =
    when (exitCase) {
      ExitCase.Completed -> Unit
      else -> state.update { ps -> ps.copy(publishers = ps.publishers.filterNot { it.token == token }) }
    }

  private fun clearSubscriber(token: Token): Unit =
    state.update { ps ->
      ps.copy(subscribers = ps.subscribers.filterNot { it.token == token })
    }

  private fun clearSubscriberOnErrorOrCancel(token: Token, exitCase: ExitCase): Unit =
    when (exitCase) {
      ExitCase.Completed -> Unit
      else -> clearSubscriber(token)
    }

  private tailrec fun consumeSubscribersLoop(
    ps: PubSub.PubSubState<I, O, QS, S>,
    queue: QS,
    remains: IQueue<PubSub.Subscriber<O, S>>,
    keep: IQueue<PubSub.Subscriber<O, S>>,
    acc: (() -> Unit)?
  ): Pair<PubSub.PubSubState<I, O, QS, S>, (() -> Unit)?> =
    when (val sub = remains.firstOrNull()) {
      null -> Pair(ps.copy(queue = queue, subscribers = keep), acc)
      else -> {
        val (queue, chunk) = strategy.get(sub.selector, queue)
        when (chunk) {
          is Some -> {
            val action = { acc?.invoke(); sub.complete(chunk.t) }
            if (!strategy.empty(queue)) consumeSubscribersLoop(ps, queue, remains.tail(), keep, action)
            else Pair(ps.copy(queue = queue, subscribers = keep.enqueue(remains.tail())), action)
          }
          None -> consumeSubscribersLoop(ps, queue, remains.tail(), keep.enqueue(sub), acc)
        }
      }
    }

  // runs all subscribers
  // yields to None, if no subscriber was completed
  // yields to Some(nextPS, completeAction) whenever at least one subscriber completes
  // before this finishes this always tries to consume all subscribers in order they have been
  // registered unless strategy signals it is empty.
  private fun consumeSubscribers(ps: PubSub.PubSubState<I, O, QS, S>): Pair<PubSub.PubSubState<I, O, QS, S>, (() -> Unit)?> =
    consumeSubscribersLoop(ps, ps.queue, ps.subscribers, IQueue.empty(), null)

  private tailrec fun publishPublishersLoop(
    ps: PubSub.PubSubState<I, O, QS, S>,
    queue: QS,
    remains: IQueue<PubSub.Publisher<I>>,
    keep: IQueue<PubSub.Publisher<I>>,
    acc: (() -> Unit)?
  ): Pair<PubSub.PubSubState<I, O, QS, S>, (() -> Unit)?> =
    when (val first = remains.firstOrNull()) {
      null -> Pair(ps.copy(queue = queue, publishers = keep), acc)
      else -> {
        if (strategy.accepts(first.i, queue)) {
          val queue1 = strategy.publish(first.i, queue)
          val action = { acc?.invoke(); first.complete() }
          publishPublishersLoop(ps, queue1, remains.tail(), keep, action)
        } else {
          publishPublishersLoop(ps, queue, remains.tail(), keep.enqueue(first), acc)
        }
      }
    }

  // tries to publish all publishers awaiting
  // yields to None if no single publisher published
  // yields to Some(nextPS, publishSignal) if at least one publisher was publishing to the queue
  // always tries to publish all publishers reminaing in single cycle, even when one publisher succeeded
  private fun publishPublishers(ps: PubSub.PubSubState<I, O, QS, S>): Pair<PubSub.PubSubState<I, O, QS, S>, (() -> Unit)?> =
    publishPublishersLoop(ps, ps.queue, ps.publishers, IQueue.empty(), null)

  /*
   * Central loop. This is consulted always to make sure there are not any not-satisfied publishers // subscribers
   * since last publish/get op
   * this tries to satisfy publishers/subscribers interchangeably until
   * there was no successful publish // subscription in the last loop
   */
  private tailrec fun loop(
    ps: PubSub.PubSubState<I, O, QS, S>,
    action: () -> Unit
  ): Pair<PubSub.PubSubState<I, O, QS, S>, (() -> Unit)> {
    val (ps1, resultPublish) = publishPublishers(ps)
    val (ps2, resultConsume) = consumeSubscribers(ps1)
    return if (resultConsume == null && resultPublish == null) Pair(ps2, action)
    else {
      val nextAction = {
        resultConsume?.invoke()
        action.invoke()
        resultPublish?.invoke()
        Unit
      }

      loop(ps2, nextAction)
    }
  }

  private fun tryGet_(
    selector: S,
    ps: PubSub.PubSubState<I, O, QS, S>
  ): Pair<PubSub.PubSubState<I, O, QS, S>, Option<O>> {
    val (queue, result) = strategy.get(selector, ps.queue)
    return Pair(ps.copy(queue = queue), result)
  }

  private fun publish_(i: I, ps: PubSub.PubSubState<I, O, QS, S>): PubSub.PubSubState<I, O, QS, S> =
    ps.copy(queue = strategy.publish(i, ps.queue))

  override suspend fun publish(a: I) =
    modify { ps ->
      if (strategy.accepts(a, ps.queue)) {
        val ps1 = publish_(a, ps)
        Pair(ps1, suspend { Unit })
      } else {
        val publisher = PubSub.Publisher(Token(), a, UnsafePromise())

        val awaitCancellable = suspend {
          guaranteeCase({ publisher.signal.join() }) { ex ->
            clearPublisherOnErrorOrCancel(publisher.token, ex)
          }
        }

        Pair(ps.copy(publishers = ps.publishers.enqueue(publisher)), awaitCancellable)
      }
    }

  override fun tryPublish(a: I): Boolean =
    update { ps ->
      if (!strategy.accepts(a, ps.queue)) Pair(ps, false)
      else {
        val ps1 = publish_(a, ps)
        Pair(ps1, true)
      }
    }

  override suspend fun get(selector: S): O =
    modify { ps ->
      val (ps, option) = tryGet_(selector, ps)
      when (option) {
        None -> {
          val token = Token()
          val sub = PubSub.Subscriber(token, selector, UnsafePromise<O>())
          val cancellableGet = suspend {
            guaranteeCase({ sub.signal.join() }) { ex ->
              clearSubscriberOnErrorOrCancel(token, ex)
            }
          }
          Pair(ps.copy(subscribers = ps.subscribers.enqueue(sub)), cancellableGet)
        }
        is Some -> Pair(ps, suspend { option.t })
      }
    }

  private suspend fun streamingGet(token: Token, selector: S): O =
    modify<O> { ps ->
      val (ps, option) = tryGet_(selector, ps)
      when (option) {
        is Some -> Pair(ps, suspend { option.t })
        None -> {
          val sub = PubSub.Subscriber(token, selector, UnsafePromise<O>())
          Pair(ps.copy(subscribers = ps.subscribers.enqueue(sub)), suspend { sub.signal.join() })
        }
      }
    }

  override fun getStream(selector: S): Stream<O> =
    Stream.bracket({ Token() }, { clearSubscriber(it) }).flatMap { token ->
      Stream.effect { streamingGet(token, selector) }.repeat()
    }

  override suspend fun tryGet(selector: S): Option<O> =
    modify { ps ->
      val (ps1, result) = tryGet_(selector, ps)
      Pair(ps1, suspend { result })
    }

  override suspend fun subscribe(selector: S): Boolean =
    modify { ps ->
      val (queue, success) = strategy.subscribe(selector, ps.queue)
      Pair(ps.copy(queue = queue), suspend { success })
    }

  override suspend fun unsubscribe(selector: S): Unit =
    modify { ps ->
      Pair(ps.copy(queue = strategy.unsubscribe(selector, ps.queue)), suspend { Unit })
    }
}

inline fun <A, B> AtomicRef<A>.modify(f: (A) -> Pair<A, B>): B =
  loop {
    val a = value
    val (u, b) = f(a)
    if (compareAndSet(a, u)) return b
  }
