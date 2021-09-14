package arrow.fx.coroutines

import arrow.core.nonFatalOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public sealed class Race3<out A, out B, out C> {
  public data class First<A>(val winner: A) : Race3<A, Nothing, Nothing>()
  public data class Second<B>(val winner: B) : Race3<Nothing, B, Nothing>()
  public data class Third<C>(val winner: C) : Race3<Nothing, Nothing, C>()

  public inline fun <D> fold(
    ifA: (A) -> D,
    ifB: (B) -> D,
    ifC: (C) -> D
  ): D = when (this) {
    is First -> ifA(winner)
    is Second -> ifB(winner)
    is Third -> ifC(winner)
  }
}

/**
 * Races the participants [fa], [fb] & [fc] in parallel on the [Dispatchers.Default].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 *
 * @see raceN for the same function that can race on any [CoroutineContext].
 */
public suspend inline fun <A, B, C> raceN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C
): Race3<A, B, C> = raceN(Dispatchers.Default, fa, fb, fc)

/**
 * Races the participants [fa], [fb] & [fc] on the provided [CoroutineContext].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb] & [fc] in parallel.
 *
 * @see raceN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C> raceN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C
): Race3<A, B, C> =
  coroutineScope {
    val a = async(ctx) { fa() }
    val b = async(ctx) { fb() }
    val c = async(ctx) { fc() }
    select<Race3<A, B, C>> {
      a.onAwait.invoke { Race3.First(it) }
      b.onAwait.invoke { Race3.Second(it) }
      c.onAwait.invoke { Race3.Third(it) }
    }.also {
      when (it) {
        is Race3.First -> cancelAndCompose(b, c)
        is Race3.Second -> cancelAndCompose(a, c)
        is Race3.Third -> cancelAndCompose(a, b)
      }
    }
  }

@PublishedApi
internal suspend fun cancelAndCompose(first: Deferred<*>, second: Deferred<*>): Unit {
  val e1 = try {
    first.cancelAndJoin()
    null
  } catch (e: Throwable) {
    e.nonFatalOrThrow()
  }
  val e2 = try {
    second.cancelAndJoin()
    null
  } catch (e: Throwable) {
    e.nonFatalOrThrow()
  }
  Platform.composeErrors(e1, e2)?.let { throw it }
}
