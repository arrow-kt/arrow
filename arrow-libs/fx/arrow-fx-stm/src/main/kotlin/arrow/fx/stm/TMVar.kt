package arrow.fx.stm

import arrow.fx.coroutines.ConcurrentVar

fun <A> STM.newTMVar(a: A): TMVar<A> = TMVar<A>(newTVar(Option.Some(a)))
fun <A> STM.newEmptyTMVar(): TMVar<A> = TMVar<A>(newTVar(Option.None))

/**
 * A [TMVar] is the [STM] analog to [ConcurrentVar].
 * It represents a reference that is either empty or full.
 *
 * The main use for [TMVar] is as a synchronization primitive as it can be used to force other transactions
 *  to wait until a [TMVar] is full.
 *
 * ## Creating a [TMVar]:
 *
 * As usual with [STM] types there are two equal sets of operators for creating them, one that can be used inside
 *  and one for use outside of transactions:
 * - [TMVar.new] and [STM.newTMVar] create a new filled [TMVar]
 * - [TMVar.empty] and [STM.newEmptyTMVar] create an empty [TMVar]
 *
 */
data class TMVar<A> internal constructor(internal val v: TVar<Option<A>>) {
  companion object {
    suspend fun <A> new(a: A): TMVar<A> = TMVar<A>(TVar.new(Option.Some(a)))
    suspend fun <A> empty(): TMVar<A> = TMVar<A>(TVar.new(Option.None))
  }
}

/**
 * ADT to avoid problems with `TMVar<A?>` because kotlin can't differ `A?` from `A??` so using a nullable
 *  reference inside `TMVar` does not work.
 */
sealed class Option<out A> {
  data class Some<A>(val a: A) : Option<A>()
  object None : Option<Nothing>()
}
