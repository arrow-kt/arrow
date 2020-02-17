package arrow.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.core.extensions.id.monad.monad
import arrow.core.value
import arrow.typeclasses.Monad
import arrow.typeclasses.internal.IdBimonad

/**
 * Alias that represents a computation that has a dependency on [D].
 */
typealias ReaderFun<D, A> = (D) -> A

/**
 * Alias ReaderHK for [ReaderTHK]
 *
 * @see ReaderTHK
 */
typealias ForReader = ForReaderT

/**
 * Alias ReaderKind for [ReaderTKind]
 *
 * @see ReaderTKind
 */
typealias ReaderOf<D, A> = ReaderTOf<ForId, D, A>

/**
 * Alias to partially apply type parameter [D] to [Reader].
 *
 * @see ReaderTKindPartial
 */
typealias ReaderPartialOf<D> = ReaderTPartialOf<ForId, D>

/**
 * [Reader] represents a computation that has a dependency on [D].
 * `Reader<D, A>` is an alias for `ReaderT<ForId, D, A>` and `Kleisli<ForId, D, A>`.
 *
 * @param D the dependency or environment we depend on.
 * @param A resulting type of the computation.
 * @see ReaderT
 */
typealias Reader<D, A> = ReaderT<ForId, D, A>

/**
 * Constructor for [Reader].
 *
 * @param run the dependency dependent computation.
 */
fun <D, A> Reader(run: ReaderFun<D, A>): Reader<D, A> = ReaderT(run.andThen { Id(it) })

/**
 * Syntax for constructing a [Reader]
 *
 * @receiver [ReaderFun] a function that represents computation dependent on type [D].
 */
fun <D, A> (ReaderFun<D, A>).reader(): Reader<D, A> = ReaderApi.lift(this)

/**
 * Alias for [Kleisli.run]
 *
 * @param d dependency to runId the computation.
 */
fun <D, A> Reader<D, A>.runId(d: D): A = this.run(d).value()

/**
 * Map the result of the computation [A] to [B] given a function [f].
 *
 * @param f the function to apply.
 */
fun <D, A, B> Reader<D, A>.map(f: (A) -> B): Reader<D, B> = map(IdBimonad, f)

/**
 * FlatMap the result of the computation [A] to another [Reader] for the same dependency [D] and flatten the structure.
 *
 * @param f the function to apply.
 */
fun <D, A, B> Reader<D, A>.flatMap(f: (A) -> Reader<D, B>): Reader<D, B> =
  Kleisli(AndThen(run).flatMap { AndThen(f(it.value()).run) })

/**
 * Apply a function `(A) -> B` that operates within the context of [Reader].
 *
 * @param ff function that maps [A] to [B] within the [Reader] context.
 */
fun <D, A, B> Reader<D, A>.ap(ff: ReaderOf<D, (A) -> B>): Reader<D, B> = ap(IdBimonad, ff)

/**
 * Zip with another [Reader].
 *
 * @param o other [Reader] to zip with.
 */
fun <D, A, B> Reader<D, A>.zip(o: Reader<D, B>): Reader<D, Tuple2<A, B>> = zip(IdBimonad, o)

/**
 * Compose with another [Reader] that has a dependency on the output of the computation.
 *
 * @param o other [Reader] to compose with.
 */
fun <D, A, C> Reader<D, A>.andThen(o: Reader<A, C>): Reader<D, C> = andThen(IdBimonad, o)

/**
 * Map the result of the computation [A] to [B] given a function [f].
 * Alias for [map]
 *
 * @param f the function to apply.
 * @see map
 */
fun <D, A, B> Reader<D, A>.andThen(f: (A) -> B): Reader<D, B> = map(f)

/**
 * Set the result to [B] after running the computation.
 */
fun <D, A, B> Reader<D, A>.andThen(b: B): Reader<D, B> = map { _ -> b }

fun Reader(): ReaderApi = ReaderApi

object ReaderApi {

  fun <D, A> just(x: A): Reader<D, A> = ReaderT.just(IdBimonad, x)

  fun <D> ask(): Reader<D, D> = ReaderT.ask(IdBimonad)

  fun <D, A> lift(run: ReaderFun<D, A>): Reader<D, A> = ReaderT(run.andThen { Id(it) })

  fun <D> monad(): Monad<ReaderPartialOf<D>> = object : Monad<ReaderPartialOf<D>> {
    override fun <A, B> Kind<ReaderPartialOf<D>, A>.flatMap(f: (A) -> Kind<ReaderPartialOf<D>, B>): Kind<ReaderPartialOf<D>, B> =
      Kleisli(AndThen(fix().run).flatMap { AndThen(f(it.value()).fix().run) })

    override fun <A> just(a: A): Kind<ReaderPartialOf<D>, A> = Reader { a }

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ReaderPartialOf<D>, Either<A, B>>): Kind<ReaderPartialOf<D>, B> =
      Kleisli.tailRecM(Id.monad(), a, f)
  }
}
