package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.identity
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.typeclasses.Monad)
 */
interface Monad<F> : Applicative<F> {

  fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>

  fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B>

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    flatMap { a -> just(f(a)) }

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> =
    ff.flatMap { f -> this.map(f) }

  fun <A> Kind<F, Kind<F, A>>.flatten(): Kind<F, A> =
    flatMap(::identity)

  fun <A, B> Kind<F, A>.followedBy(fb: Kind<F, B>): Kind<F, B> =
    flatMap { fb }

  fun <A, B> Kind<F, A>.followedByEval(fb: Eval<Kind<F, B>>): Kind<F, B> =
    flatMap { fb.value() }

  fun <A, B> Kind<F, A>.effectM(f: (A) -> Kind<F, B>): Kind<F, A> =
    flatMap { a -> f(a).map { a } }

  fun <A, B> Kind<F, A>.forEffect(fb: Kind<F, B>): Kind<F, A> =
    flatMap { a -> fb.map { a } }

  fun <A, B> Kind<F, A>.forEffectEval(fb: Eval<Kind<F, B>>): Kind<F, A> =
    flatMap { a -> fb.value().map { a } }

  fun <A, B> Kind<F, A>.mproduct(f: (A) -> Kind<F, B>): Kind<F, Tuple2<A, B>> =
    flatMap { a -> f(a).map { Tuple2(a, it) } }

  fun <B> Kind<F, Boolean>.ifM(ifTrue: () -> Kind<F, B>, ifFalse: () -> Kind<F, B>): Kind<F, B> =
    flatMap { if (it) ifTrue() else ifFalse() }

  /**
   * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
   * A coroutine is initiated and suspended inside [MonadErrorContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   */
  fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    val wrapReturn: suspend MonadContinuation<F, *>.() -> Kind<F, B> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }

}