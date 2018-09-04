package arrow.data

import arrow.core.*
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
@Suppress("FunctionName")
fun <D, A> Reader(run: ReaderFun<D, A>): Reader<D, A> = ReaderT(run.andThen { Id(it) })

/**
 * Syntax for constructing a [Reader]
 *
 * @receiver [ReaderFun] a function that represents computation dependent on type [D].
 */
fun <D, A> (ReaderFun<D, A>).reader(): Reader<D, A> = Reader().lift(this)

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
fun <D, A, B> Reader<D, A>.flatMap(f: (A) -> Reader<D, B>): Reader<D, B> = flatMap(IdBimonad, f)

/**
 * Apply a function `(A) -> B` that operates within the context of [Reader].
 *
 * @param ff function that maps [A] to [B] within the [Reader] context.
 */
fun <D, A, B> ReaderOf<D, A>.apPipe(ff: ReaderOf<D, (A) -> B>): Reader<D, B> =
  fix().apPipe(IdBimonad, ff)

/**
 * Apply this function which operates within the context of [Reader] to a value in the context of
 * [Reader].
 *
 * Version of [apPipe] with flipped receiver and parameter.
 *
 * @param fa value that is mapped to [B] by this function within the [Reader] context.
 */
@Suppress("NOTHING_TO_INLINE")
inline infix fun <D, A, B> ReaderOf<D, (A) -> B>.ap(fa: ReaderOf<D, A>): ReaderOf<D, B> =
  fa.apPipe(this)

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

@Suppress("FunctionName")
fun Reader(): ReaderApi = ReaderApi

object ReaderApi {

  fun <D, A> just(x: A): Reader<D, A> = ReaderT.just(IdBimonad, x)

  fun <D> ask(): Reader<D, D> = ReaderT.ask(IdBimonad)

  fun <D, A> lift(run: ReaderFun<D, A>): Reader<D, A> = ReaderT(run.andThen { Id(it) })
}
