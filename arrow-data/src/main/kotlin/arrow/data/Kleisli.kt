package arrow.data

import arrow.*
import arrow.core.Either
import arrow.core.Tuple2
import arrow.typeclasses.*

/**
 * Alias that represents an arrow from [D] to a monadic value `HK<F, A>`
 */
typealias KleisliFun<F, D, A> = (D) -> HK<F, A>

/**
 * [Kleisli] represents an arrow from [D] to a monadic value `HK<F, A>`.
 *
 * @param F the context of the result.
 * @param D the dependency or environment we depend on.
 * @param A resulting type of the computation.
 * @property run the arrow from [D] to `HK<F, A>`.
 */
@higherkind
class Kleisli<F, D, A> private constructor(val run: KleisliFun<F, D, A>, dummy: Unit = Unit) : KleisliKind<F, D, A>, KleisliKindedJ<F, D, A> {

    /**
     * Apply a function `(A) -> B` that operates within the [Kleisli] context.
     *
     * @param ff function with the [Kleisli] context.
     * @param AF [Applicative] for the context [F].
     */
    fun <B> ap(ff: KleisliKind<F, D, (A) -> B>, AF: Applicative<F>): Kleisli<F, D, B> =
            Kleisli { AF.ap(run(it), ff.ev().run(it)) }

    /**
     * Map the end of the arrow [A] to [B] given a function [f].
     *
     * @param f the function to apply.
     * @param FF [Functor] for the context [F].
     */
    fun <B> map(f: (A) -> B, FF: Functor<F>): Kleisli<F, D, B> = Kleisli { a -> FF.map(run(a)) { f(it) } }

    /**
     * FlatMap the end of the arrow [A] to another [Kleisli] arrow for the same start [D] and context [F].
     *
     * @param f the function to flatmap.
     * @param MF [Monad] for the context [F].
     */
    fun <B> flatMap(f: (A) -> Kleisli<F, D, B>, MF: Monad<F>): Kleisli<F, D, B> =
            Kleisli { d ->
                MF.flatMap(run(d)) { a -> f(a).run(d) }
            }

    /**
     * Zip with another [Kleisli] arrow.
     *
     * @param o other [Kleisli] to zip with.
     * @param MF [Monad] for the context [F].
     */
    fun <B> zip(o: Kleisli<F, D, B>, MF: Monad<F>): Kleisli<F, D, Tuple2<A, B>> =
            flatMap({ a ->
                o.map({ b -> Tuple2(a, b) }, MF)
            }, MF)

    /**
     * Compose this arrow with another function to transform the input of the arrow.
     *
     * @param f function that transforms new arrow head [DD] to [D].
     */
    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli { dd -> run(f(dd)) }

    /**
     * Compose with another [Kleisli].
     *
     * @param o other [Kleisli] to compose with.
     * @param MF [Monad] for the context [F].
     */
    fun <C> andThen(f: Kleisli<F, A, C>, MF: Monad<F>): Kleisli<F, D, C> = andThen(f.run, MF)

    /**
     * Compose with a function to transform the output of the [Kleisli] arrow.
     *
     * @param f the function to apply.
     * @param MF [Monad] for the context [F].
     */
    fun <B> andThen(f: (A) -> HK<F, B>, MF: Monad<F>): Kleisli<F, D, B> = Kleisli { MF.flatMap(run(it), f) }

    /**
     * Set the end of the arrow to `HK<F, B>` after running the computation.
     *
     * @param fb the new end of the arrow.
     * @param MF [Monad] for the context [F].
     */
    fun <B> andThen(fb: HK<F, B>, MF: Monad<F>): Kleisli<F, D, B> = andThen({ fb }, MF)

    /**
     * Handle error within context of [F] given a [MonadError] is defined for [F].
     *
     * @param f function to handle error.
     * @param ME [MonadError] for the context [F].
     */
    fun <E> handleErrorWith(f: (E) -> KleisliKind<F, D, A>, ME: MonadError<F, E>): Kleisli<F, D, A> = Kleisli {
        ME.handleErrorWith(run(it), { e: E -> f(e).ev().run(it) })
    }

    companion object {

        /**
         * Constructor to create `Kleisli<F, D, A>` given a [KleisliFun].
         *
         * @param run the arrow from [D] to a monadic value `HK<F, A>`
         */
        operator fun <F, D, A> invoke(run: KleisliFun<F, D, A>): Kleisli<F, D, A> = Kleisli(run, Unit)

        /**
         * Tail recursive function that keeps calling [f] until [arrow.Either.Right] is returned.
         *
         * @param a initial value to start running recursive call to [f]
         * @param f function that is called recusively until [arrow.Either.Right] is returned.
         * @param MF [Monad] for the context [F].
         */
        fun <F, D, A, B> tailRecM(a: A, f: (A) -> KleisliKind<F, D, Either<A, B>>, MF: Monad<F>): Kleisli<F, D, B> =
                Kleisli { b -> MF.tailRecM(a, { f(it).ev().run(b) }) }

        /**
         * Create an arrow for a value of [A].
         *
         * @param x value of [A].
         * @param AF [Applicative] for context [F].
         */
        inline fun <reified F, D, A> pure(x: A, AF: Applicative<F> = applicative<F>()): Kleisli<F, D, A> = Kleisli { _ -> AF.pure(x) }

        /**
         * Ask an arrow from [D] to [D].
         *
         * @param AF [Applicative] for context [F].
         */
        inline fun <reified F, D> ask(AF: Applicative<F> = applicative<F>()): Kleisli<F, D, D> = Kleisli { AF.pure(it) }

        /**
         * Raise an error [E].
         * @param ME [MonadError] for context [F].
         */
        fun <F, D, E, A> raiseError(e: E, ME: MonadError<F, E>): Kleisli<F, D, A> = Kleisli { ME.raiseError(e) }

    }

}

/**
 * Flatten nested [Kleisli] arrows.
 *
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(MF: Monad<F>): Kleisli<F, D, A> = flatMap({ it }, MF)

/**
 * Syntax for constructing a [Kleisli]
 *
 * @receiver [KleisliFun] a function that represents computation dependent on [D] with the result in context [F].
 */
fun <F, D, A> KleisliFun<F, D, A>.kleisli(): Kleisli<F, D, A> = Kleisli(this)

/**
 * Alias that represents a computation that has a dependency on [D].
 */
typealias ReaderTFun<F, D, A> = KleisliFun<F, D, A>

/**
 * Alias ReaderTHK for [KleisliHK]
 *
 * @see KleisliHK
 */
typealias ReaderTHK = KleisliHK

/**
 * Alias ReaderTKind for [KleisliKind]
 *
 * @see KleisliKind
 */
typealias ReaderTKind<F, D, A> = KleisliKind<F, D, A>

/**
 * Alias to partially apply type parameter [F] and [D] to [ReaderT].
 *
 * @see KleisliKindPartial
 */
typealias ReaderTKindPartial<F, D> = KleisliKindPartial<F, D>

/**
 * [Reader] represents a computation that has a dependency on [D] with a result within context [F].
 * `ReaderT<F, D, A>` is the monad transfomer variant of [Reader] and an alias for `Kleisli<F, D, A>`.
 *
 * @param F the context of the result.
 * @param D the dependency or environment we depend on.
 * @param A resulting type of the computation.
 * @see Kleisli
 */
typealias ReaderT<F, D, A> = Kleisli<F, D, A>

/**
 * Syntax for constructing a [ReaderT]
 *
 * @receiver [ReaderTFun] a function that represents computation dependent on [D] with the result in context [F].
 */
fun <F, D, A> ReaderTFun<F, D, A>.readerT(): ReaderT<F, D, A> = ReaderT(this)