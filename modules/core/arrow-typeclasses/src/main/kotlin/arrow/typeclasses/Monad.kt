package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.identity
import arrow.documented
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.typeclasses.Monad)
 *
 * [Monad] abstract over the ability to declare sequential computations that are dependent in the order or
 * the results of previous computations.
 *
 * Given a type constructor [F] with a value of [A] we can compose multiple operations of type
 * `Kind<F, ?>` where `?` denotes a value being transformed.
 *
 * This is true for all type constructors that can support the [Monad] type class including and not limited to
 * [IO], [DeferredK], [ObservableK], [Option], [Either], [List], [Try] ...
 *
 * [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 *
 */
@documented
interface Monad<F> : Applicative<F> {

  fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>

  fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B>

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    flatMap { a -> just(f(a)) }

  /**
   * @see [Applicative.ap]
   */
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
  @Deprecated(
    "`binding` is getting renamed to `fx` for consistency with the Arrow Fx system. Use the Fx extensions for comprehensions",
    ReplaceWith("fx")
  )
  fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    fx(c)

  fun <A> fx(c: suspend MonadContinuation<F, *>.() -> A): Kind<F, A> {
    val continuation = MonadContinuation<F, A>(this)
    val wrapReturn: suspend MonadContinuation<F, *>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }

}