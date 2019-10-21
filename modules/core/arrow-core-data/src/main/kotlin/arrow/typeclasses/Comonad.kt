package arrow.typeclasses

import arrow.Kind
import arrow.core.identity
import java.io.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.typeclasses.Comonad)
 *
 * The dual of monads, used to extract values from F
 */
interface Comonad<F> : Functor<F> {

  /**
   * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
   * A coroutine is initiated and suspended inside [MonadThrowContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   */
  val fx: ComonadFx<F>
    get() = object : ComonadFx<F> {
      override val CM: Comonad<F> = this@Comonad
    }

  fun <A, B> Kind<F, A>.coflatMap(f: (Kind<F, A>) -> B): Kind<F, B>

  fun <A> Kind<F, A>.extract(): A

  fun <A> Kind<F, A>.duplicate(): Kind<F, Kind<F, A>> =
    coflatMap(::identity)
}

@RestrictsSuspension
interface ComonadSyntax<F> : Comonad<F> {
  suspend fun <B> Kind<F, B>.fix(): B
  suspend fun <B> extract(m: () -> Kind<F, B>): B
}

open class ComonadContinuation<F, A : Any>(CM: Comonad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  Serializable, Continuation<A>, Comonad<F> by CM, ComonadSyntax<F> {

  override fun resumeWith(result: Result<A>): Unit =
    result.fold({ returnedMonad = it }, { throw it })

  override fun resume(value: A) {
    returnedMonad = value
  }

  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  internal lateinit var returnedMonad: A

  override suspend fun <B> Kind<F, B>.fix(): B = extract { this }

  override suspend fun <B> extract(m: () -> Kind<F, B>): B = suspendCoroutineUninterceptedOrReturn { c ->
    val labelHere = c.stateStack // save the whole coroutine stack labels
    returnedMonad = m().coflatMap { x: Kind<F, B> ->
      c.stateStack = labelHere
      c.resume(x.extract())
      returnedMonad
    }.extract()
    COROUTINE_SUSPENDED
  }
}

@Deprecated(
  "`cobinding` is getting renamed to `fx` for consistency with the Arrow Fx system. Use the Fx extensions for comprehensions",
  ReplaceWith("fx.comonad(c)")
)
fun <F, B : Any> Comonad<F>.cobinding(c: suspend ComonadSyntax<F>.() -> B): B {
  val continuation = ComonadContinuation<F, B>(this)
  c.startCoroutine(continuation, continuation)
  return continuation.returnedMonad
}

interface ComonadFx<F> {
  val CM: Comonad<F>

  /**
   * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
   * A coroutine is initiated and inside `MonadContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   */
  fun <B : Any> comonad(c: suspend ComonadSyntax<F>.() -> B): B {
    val continuation = ComonadContinuation<F, B>(CM)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
  }
}
