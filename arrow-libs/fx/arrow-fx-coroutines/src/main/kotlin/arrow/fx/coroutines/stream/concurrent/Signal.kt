package arrow.fx.coroutines.stream.concurrent

import arrow.core.Option
import arrow.core.Some
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.Token
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.flatMap
import arrow.fx.coroutines.stream.stream

/** Pure holder of a single value of type `A` that can be read in the effect `F`. */
interface Signal<A> {

  /**
   * Returns a stream of the updates to this signal.
   *
   * Updates that are very close together may result in only the last update appearing
   * in the stream. If you want to be notified about every single update, use
   * a `Queue` instead.
   */
  fun discrete(): Stream<A>

  /**
   * Returns a stream of the current value of the signal. An element is always
   * available -- on each pull, the current value is supplied.
   */
  fun continuous(): Stream<A>

  /**
   * Asynchronously gets the current value of this `Signal`.
   */
  suspend fun get(): A

  fun <B> map(f: (A) -> B): Signal<B> =
    object : Signal<B> {
      override fun discrete(): Stream<B> = this@Signal.discrete().map(f)
      override fun continuous(): Stream<B> = this@Signal.continuous().map(f)
      override suspend fun get(): B = f(this@Signal.get())
    }

  companion object {
    fun <A> constant(a: A): Signal<A> =
      object : Signal<A> {
        override fun discrete(): Stream<A> = Stream(a).apply { Stream.never<A>() }
        override fun continuous(): Stream<A> = Stream.constant(a)
        override suspend fun get(): A = a
      }
  }
}

fun <O> Signal<Boolean>.interrupt(stream: Stream<O>): Stream<O> =
  stream.interruptWhen(this)

/**
 * Pure holder of a single atomic value of type `A` that can be both read and updated.
 * Composes [Signal] and [Atomic] together to make an signalling atomic value.
 */
class SignallingAtomic<A> internal constructor(
  private val pubSub: PubSub<Pair<Long, Pair<Long, A>>, A, Option<Token>>,
  private val atomic: Atomic<Pair<Long, A>>
) : Atomic<A>, Signal<A> {

  companion object {
    suspend operator fun <A> invoke(initial: A): SignallingAtomic<A> {
      val pubSub = PubSub.from(PubSub.Strategy.discrete(0, initial))
      val atomic = Atomic(Pair(0L, initial))
      return SignallingAtomic(pubSub, atomic)
    }

    fun <A> unsafe(initial: A): SignallingAtomic<A> {
      val pubSub = PubSub.unsafe(PubSub.Strategy.discrete(0, initial))
      val atomic = Atomic.unsafe(Pair(0L, initial))
      return SignallingAtomic(pubSub, atomic)
    }
  }

  override suspend fun get(): A =
    atomic.get().second

  override suspend fun set(a: A) =
    update { a }

  override suspend fun getAndSet(a: A): A =
    modify { old -> Pair(a, old) }

  override suspend fun setAndGet(a: A): A =
    updateAndGet { a }

  override suspend fun update(f: (A) -> A): Unit =
    modify { a -> Pair(f(a), Unit) }

  override suspend fun getAndUpdate(f: (A) -> A): A =
    modify { a -> Pair(f(a), a) }

  override suspend fun updateAndGet(f: (A) -> A): A =
    modifyGet { a -> Pair(f(a), Unit) }.first

  override suspend fun <B> modify(f: (A) -> Pair<A, B>): B {
    val (signal: Pair<Long, Pair<Long, A>>, b: B) = atomic.modify { modify_(f, it) }
    ForkConnected { pubSub.publish(signal) }
    return b
  }

  override suspend fun <B> modifyGet(f: (A) -> Pair<A, B>): Pair<A, B> {
    val (signal, b) = atomic.modifyGet { modify_(f, it) }
    ForkConnected { pubSub.publish(b.first) }
    return Pair(signal.second, b.second)
  }

  override suspend fun tryUpdate(f: (A) -> A): Boolean =
    tryModify { a ->
      Pair(f(a), Unit)
    }?.let { true } ?: false

  override suspend fun <B> tryModify(f: (A) -> Pair<A, B>): B? =
    atomic.tryModify { modify_(f, it) }?.let { (signal, b) ->
      ForkConnected { pubSub.publish(signal) }
      b
    }

  override suspend fun access(): Pair<A, suspend (A) -> Boolean> {
    val (access, setter) = atomic.access()
    val action: suspend (A) -> Boolean = { a ->
      val success = setter(Pair(access.first + 1, a))
      if (success) ForkConnected { pubSub.publish(Pair(access.first, Pair(access.first + 1, a))) }
      success
    }

    return Pair(access.second, action)
  }

  override fun discrete(): Stream<A> =
    Stream.bracket({ Some(Token()) }, { token -> pubSub.unsubscribe(token) })
      .flatMap { token -> pubSub.getStream(token) }

  override fun continuous(): Stream<A> =
    Stream.effect { get() }.repeat()

  private fun <B> modify_(
    f: (A) -> Pair<A, B>,
    stamped: Pair<Long, A>
  ): Pair<Pair<Long, A>, Pair<Pair<Long, Pair<Long, A>>, B>> {
    val (a1, b) = f(stamped.second)
    val stamp = stamped.first + 1
    return Pair(Pair(stamp, a1), Pair(Pair(stamped.first, Pair(stamp, a1)), b))
  }
}
