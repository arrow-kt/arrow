package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.identity
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

/**
 * Alias that represents a function from [D] to a monadic value `Kind<F, A>`
 */
typealias KleisliFun<F, D, A> = (D) -> Kind<F, A>

fun <F, D, A> KleisliOf<F, D, A>.run(d: D): Kind<F, A> = fix().run(d)

/**
 * [Kleisli] represents a function parameter from [D] to a value `Kind<F, A>`.
 *
 * @param F the context of the result.
 * @param D the dependency or environment we depend on.
 * @param A resulting type of the computation.
 * @property run the arrow from [D] to `Kind<F, A>`.
 */
@higherkind
class Kleisli<F, D, A>(val run: KleisliFun<F, D, A>) : KleisliOf<F, D, A>, KleisliKindedJ<F, D, A> {

  /**
   * Apply a function `(A) -> B` that operates within the [Kleisli] context.
   *
   * @param ff function with the [Kleisli] context.
   * @param AF [Applicative] for the context [F].
   */
  fun <B> ap(AF: Applicative<F>, ff: KleisliOf<F, D, (A) -> B>): Kleisli<F, D, B> =
    AF.run { Kleisli { run(it).ap(ff.run(it)) } }

  /**
   * Map the end of the arrow [A] to [B] given a function [f].
   *
   * @param f the function to apply.
   * @param FF [Functor] for the context [F].
   */
  fun <B> map(FF: Functor<F>, f: (A) -> B): Kleisli<F, D, B> = FF.run {
    Kleisli { d -> run(d).map(f) }
  }

  /**
   * FlatMap the end of the arrow [A] to another [Kleisli] arrow for the same start [D] and context [F].
   *
   * @param f the function to flatmap.
   * @param MF [Monad] for the context [F].
   */
  fun <B> flatMap(MF: Monad<F>, f: (A) -> KleisliOf<F, D, B>): Kleisli<F, D, B> = MF.run {
    Kleisli { d ->
      run(d).flatMap { a -> f(a).run(d) }
    }
  }

  /**
   * Zip with another [Kleisli] arrow.
   *
   * @param o other [Kleisli] to zip with.
   * @param MF [Monad] for the context [F].
   */
  fun <B> zip(MF: Monad<F>, o: KleisliOf<F, D, B>): Kleisli<F, D, Tuple2<A, B>> =
    flatMap(MF) { a ->
      o.fix().map(MF) { b -> Tuple2(a, b) }
    }

  /**
   * Compose this arrow with another function to transform the input of the arrow.
   *
   * @param f function that transforms new arrow head [DD] to [D].
   */
  fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> =
    Kleisli { dd -> run(f(dd)) }

  /**
   * Compose with another [Kleisli].
   *
   * @param o other [Kleisli] to compose with.
   * @param MF [Monad] for the context [F].
   */
  fun <C> andThen(MF: Monad<F>, o: Kleisli<F, A, C>): Kleisli<F, D, C> =
    andThen(MF, o.run)

  /**
   * Compose with a function to transform the output of the [Kleisli] arrow.
   *
   * @param f the function to apply.
   * @param MF [Monad] for the context [F].
   */
  fun <B> andThen(MF: Monad<F>, f: (A) -> Kind<F, B>): Kleisli<F, D, B> = MF.run {
    Kleisli { d -> run(d).flatMap(f) }
  }

  /**
   * Set the end of the arrow to `Kind<F, B>` after running the computation.
   *
   * @param fb the new end of the arrow.
   * @param MF [Monad] for the context [F].
   */
  fun <B> andThen(MF: Monad<F>, fb: Kind<F, B>): Kleisli<F, D, B> =
    andThen(MF) { fb }

  /**
   * Handle error within context of [F] given a [ApplicativeError] is defined for [F].
   *
   * @param f function to handle error.
   * @param AE [ApplicativeError] for the context [F].
   */
  fun <E> handleErrorWith(AE: ApplicativeError<F, E>, f: (E) -> KleisliOf<F, D, A>): Kleisli<F, D, A> = AE.run {
    Kleisli { d -> run(d).handleErrorWith { e -> f(e).run(d) } }
  }

  companion object {

    /**
     * Constructor to create `Kleisli<F, D, A>` given a [KleisliFun].
     *
     * @param run the arrow from [D] to a monadic value `Kind<F, A>`
     */
    operator fun <F, D, A> invoke(run: KleisliFun<F, D, A>): Kleisli<F, D, A> =
      Kleisli(run)

    /**
     * Tail recursive function that keeps calling [f] until [Either.Right] is returned.
     *
     * @param a initial value to start running recursive call to [f]
     * @param f function that is called recusively until [Either.Right] is returned.
     * @param MF [Monad] for the context [F].
     */
    fun <F, D, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> KleisliOf<F, D, Either<A, B>>): Kleisli<F, D, B> =
      Kleisli { d -> MF.tailRecM(a) { f(it).run(d) } }

    /**
     * Create an arrow for a value of [A].
     *
     * @param x value of [A].
     * @param AF [Applicative] for context [F].
     */
    fun <F, D, A> just(AF: Applicative<F>, x: A): Kleisli<F, D, A> =
      Kleisli { _ -> AF.just(x) }

    /**
     * Ask an arrow from [D] to [D].
     *
     * @param AF [Applicative] for context [F].
     */
    fun <F, D> ask(AF: Applicative<F>): Kleisli<F, D, D> =
      Kleisli { AF.just(it) }

    /**
     * Raise an error [E].
     * @param AE [ApplicativeError] for context [F].
     */
    fun <F, D, E, A> raiseError(AE: ApplicativeError<F, E>, e: E): Kleisli<F, D, A> =
      Kleisli { AE.raiseError(e) }

    /**
     * Lift a value of [F] into [Kleisli]
     * @param fa value to lift for context [F].
     */
    fun <F, D, A> liftF(fa: Kind<F, A>): Kleisli<F, D, A> =
      Kleisli { _ -> fa }
  }
}

/**
 * Flatten nested [Kleisli] arrows.
 *
 * @param MF [Monad] for the context [F].
 */
fun <F, D, A> KleisliOf<F, D, Kleisli<F, D, A>>.flatten(MF: Monad<F>): Kleisli<F, D, A> = fix().flatMap(MF, ::identity)

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
typealias ForReaderT = ForKleisli

/**
 * Alias ReaderTKind for [KleisliKind]
 *
 * @see KleisliKind
 */
typealias ReaderTOf<F, D, A> = KleisliOf<F, D, A>

/**
 * Alias to partially apply type parameter [F] and [D] to [ReaderT].
 *
 * @see KleisliKindPartial
 */
typealias ReaderTPartialOf<F, D> = KleisliPartialOf<F, D>

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
