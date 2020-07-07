package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

fun <A, T> ConstOf<A, T>.value(): A = this.fix().value()

@higherkind
data class Const<A, out T>(private val value: A) : ConstOf<A, T> {

  @Suppress("UNCHECKED_CAST")
  fun <U> retag(): Const<A, U> =
    this as Const<A, U>

  @Suppress("UNUSED_PARAMETER")
  fun <G, U> traverse(GA: Applicative<G>, f: (T) -> Kind<G, U>): Kind<G, Const<A, U>> =
    GA.just(retag())

  @Suppress("UNUSED_PARAMETER")
  fun <G, U> traverseFilter(GA: Applicative<G>, f: (T) -> Kind<G, Option<U>>): Kind<G, Const<A, U>> =
    GA.just(retag())

  companion object {
    fun <A, T> just(a: A): Const<A, T> =
      Const(a)
  }

  fun value(): A =
    value

  fun show(SA: Show<A>): String =
    "$Const(${SA.run { value.show() }})"

  override fun toString(): String =
    show(Show.any())
}

fun <A, T> ConstOf<A, T>.combine(SG: Semigroup<A>, that: ConstOf<A, T>): Const<A, T> =
  Const(SG.run { value().combine(that.value()) })

inline fun <A, T, U> ConstOf<A, T>.ap(SG: Semigroup<A>, ff: ConstOf<A, (T) -> U>): Const<A, U> =
  fix().retag<U>().combine(SG, ff.fix().retag())

fun <T, A, G> ConstOf<A, Kind<G, T>>.sequence(GA: Applicative<G>): Kind<G, Const<A, T>> =
  fix().traverse(GA, ::identity)

inline fun <A> A.const(): Const<A, Nothing> =
  Const(this)

fun <A, T> const(c: suspend EagerBind<ConstPartialOf<A>>.() -> A): Const<A, T> {
  val continuation: ConstContinuation<A, A> = ConstContinuation()
  return continuation.startCoroutineUninterceptedAndReturn {
    Const.just(c())
  } as Const<A, T>
}

suspend fun <A, T> const(c: suspend BindSyntax<ConstPartialOf<A>>.() -> A): Const<A, T> =
  suspendCoroutineUninterceptedOrReturn { cont ->
    val continuation = ConstSContinuation(cont as Continuation<ConstOf<A, T>>)
    continuation.startCoroutineUninterceptedOrReturn {
      Const.just(c())
    }
  }

internal class ConstSContinuation<A, T>(
  parent: Continuation<ConstOf<A, T>>
) : SuspendMonadContinuation<ConstPartialOf<A>, T>(parent) {
  override fun ShortCircuit.recover(): Const<A, T> =
    throw this

  override suspend fun <B> Kind<ConstPartialOf<A>, B>.bind(): B =
    value() as B
}

internal class ConstContinuation<A, T> : MonadContinuation<ConstPartialOf<A>, T>() {
  override fun ShortCircuit.recover(): Const<A, T> =
    throw this

  override suspend fun <B> Kind<ConstPartialOf<A>, B>.bind(): B =
    value() as B
}
