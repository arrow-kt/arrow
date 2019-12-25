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
 * [IO], [ObservableK], [Option], [Either], [List], [Try] ...
 *
 * [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 *
 */
@documented
interface Monad<F> : Selective<F> {

  /**
   * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
   * A coroutine is initiated and suspended inside [MonadThrowContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   */
  val fx: MonadFx<F>
    get() = object : MonadFx<F> {
      override val M: Monad<F> = this@Monad
    }

  fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>

  fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B>

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    flatMap { a -> just(f(a)) }

  /** @see [Apply.ap] */
  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> =
    flatMap { a -> ff.map { f -> f(a) } }

  fun <A> Kind<F, Kind<F, A>>.flatten(): Kind<F, A> =
    flatMap(::identity)

  override fun <A, B> Kind<F, A>.followedBy(fb: Kind<F, B>): Kind<F, B> =
    flatMap { fb }

  override fun <A, B> Kind<F, A>.apTap(fb: Kind<F, B>): Kind<F, A> =
    flatTap { fb }

  fun <A, B> Kind<F, A>.followedByEval(fb: Eval<Kind<F, B>>): Kind<F, B> =
    flatMap { fb.value() }

  @Deprecated(
    "effectM is being renamed to flatTap",
    ReplaceWith("flatTap(f)")
  )
  fun <A, B> Kind<F, A>.effectM(f: (A) -> Kind<F, B>): Kind<F, A> =
    flatTap(f)

  fun <A, B> Kind<F, A>.flatTap(f: (A) -> Kind<F, B>): Kind<F, A> =
    flatMap { a -> f(a).map { a } }

  fun <A, B> Kind<F, A>.productL(fb: Kind<F, B>): Kind<F, A> =
    flatMap { a -> fb.map { a } }

  @Deprecated(
    "forEffect is being renamed to productL",
    ReplaceWith("productL(fb)")
  )
  fun <A, B> Kind<F, A>.forEffect(fb: Kind<F, B>): Kind<F, A> =
    productL(fb)

  fun <A, B> Kind<F, A>.productLEval(fb: Eval<Kind<F, B>>): Kind<F, A> =
    flatMap { a -> fb.value().map { a } }

  @Deprecated(
    "forEffectEval is being renamed to productLEval",
    ReplaceWith("productLEval(fb)")
  )
  fun <A, B> Kind<F, A>.forEffectEval(fb: Eval<Kind<F, B>>): Kind<F, A> =
    productLEval(fb)

  fun <A, B> Kind<F, A>.mproduct(f: (A) -> Kind<F, B>): Kind<F, Tuple2<A, B>> =
    flatMap { a -> f(a).map { Tuple2(a, it) } }

  fun <B> Kind<F, Boolean>.ifM(ifTrue: () -> Kind<F, B>, ifFalse: () -> Kind<F, B>): Kind<F, B> =
    flatMap { if (it) ifTrue() else ifFalse() }

  fun <A, B> Kind<F, Either<A, B>>.selectM(f: Kind<F, (A) -> B>): Kind<F, B> =
    flatMap { it.fold({ a -> f.map { ff -> ff(a) } }, { b -> just(b) }) }

  override fun <A, B> Kind<F, Either<A, B>>.select(f: Kind<F, (A) -> B>): Kind<F, B> = selectM(f)

  override fun <A, B> Kind<F, A>.lazyAp(ff: () -> Kind<F, (A) -> B>): Kind<F, B> =
    flatMap { a -> ff().map { f -> f(a) } }
}

interface MonadFx<F> {
  val M: Monad<F>
  fun <A> monad(c: suspend MonadSyntax<F>.() -> A): Kind<F, A> {
    val continuation = MonadContinuation<F, A>(M)
    val wrapReturn: suspend MonadContinuation<F, *>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}
