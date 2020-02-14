package arrow.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.SemigroupK

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>` with a result in certain context `F`.
 */
typealias StateTFun<F, S, A> = (S) -> Kind<F, Tuple2<S, A>>

/**
 * Run the stateful computation within the context `F`.
 *
 * @param initial state to start stateful computation
 */
fun <F, S, A> StateTOf<F, S, A>.run(initial: S): Kind<F, Tuple2<S, A>> = fix().runF(initial)

/**
 * `StateT<F, S, A>` is a stateful computation within a context `F` yielding
 * a value of type `A`. i.e. StateT<EitherPartialOf<E>, S, A> = Either<E, State<S, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param S the state we are performing computation upon.
 * @param A current value of computation.
 * @param runF the stateful computation that is wrapped and managed by `StateT`
 */
@higherkind
class StateT<F, S, A> private constructor(
  val runF: StateTFun<F, S, A>
) : StateTOf<F, S, A>, StateTKindedJ<F, S, A> {

  companion object {

    operator fun <F, S, A> invoke(f: (S) -> Kind<F, Tuple2<S, A>>): StateT<F, S, A> = StateT(f)

    fun <F, S, T> just(AF: Applicative<F>, t: T): StateT<F, S, T> =
      StateT { s -> AF.just(s toT t) }

    /**
     * Lift a value of type `Kind<F, A>` into `StateT<F, S, A>`.
     *
     * @param AF [Applicative] for the context [F].
     * @param fa the value to liftF.
     */
    fun <F, S, A> liftF(AF: Applicative<F>, fa: Kind<F, A>): StateT<F, S, A> = AF.run {
      StateT { s -> fa.map { a -> Tuple2(s, a) } }
    }

    /**
     * Return input without modifying it.
     *
     * @param AF [Applicative] for the context [F].
     */
    fun <F, S> get(AF: Applicative<F>): StateT<F, S, S> =
      StateT { s -> AF.just(Tuple2(s, s)) }

    /**
     * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
     *
     *
     *
     * @param AF [Applicative] for the context [F].
     * @param f the function applied to inspect [T] from [S].
     */
    fun <F, S, T> inspect(AF: Applicative<F>, f: (S) -> T): StateT<F, S, T> =
      StateT { s -> AF.just(Tuple2(s, f(s))) }

    /**
     * Modify the state with [f] `(S) -> S` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param f the modify function to apply.
     */
    fun <F, S> modify(AF: Applicative<F>, f: (S) -> S): StateT<F, S, Unit> =
      StateT { s ->
        AF.run {
          just(f(s)).map { Tuple2(it, Unit) }
        }
      }

    /**
     * Modify the state with an [Applicative] function [f] `(S) -> Kind<F, S>` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param f the modify function to apply.
     */
    fun <F, S> modifyF(AF: Applicative<F>, f: (S) -> Kind<F, S>): StateT<F, S, Unit> =
      StateT { s -> AF.run { f(s).map { Tuple2(it, Unit) } } }

    /**
     * Set the state to a value [s] and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param s value to set.
     */
    fun <F, S> set(AF: Applicative<F>, s: S): StateT<F, S, Unit> =
      StateT { _ -> AF.just(Tuple2(s, Unit)) }

    /**
     * Set the state to a value [s] of type `Kind<F, S>` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param s value to set.
     */
    fun <F, S> setF(AF: Applicative<F>, s: Kind<F, S>): StateT<F, S, Unit> =
      StateT { _ -> AF.run { s.map { Tuple2(it, Unit) } } }

    /**
     * Tail recursive function that keeps calling [f]  until [arrow.Either.Right] is returned.
     *
     * @param MF [Monad] for the context [F].
     * @param a initial value to start running recursive call to [f]
     * @param f function that is called recusively until [arrow.Either.Right] is returned.
     */
    fun <F, S, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> StateTOf<F, S, Either<A, B>>): StateT<F, S, B> = MF.run {
      StateT { s: S ->
        tailRecM(Tuple2(s, a)) { (s, a0) ->
          f(a0).run(s).map { (s, ab) ->
            ab.bimap({ a1 -> Tuple2(s, a1) }, { b -> Tuple2(s, b) })
          }
        }
      }
    }
  }

  /**
   * Map current value [A] given a function [f].
   *
   * @param FF [Functor] for the context [F].
   * @param f the function to apply.
   */
  fun <B> map(FF: Functor<F>, f: (A) -> B): StateT<F, S, B> = transform(FF) { (s, a) -> Tuple2(s, f(a)) }

  /**
   * Apply a function `(S) -> B` that operates within the [StateT] context.
   *
   * @param MF [Monad] for the context [F].
   * @param ff function with the [StateT] context.
   */
  fun <B> ap(MF: Monad<F>, ff: StateTOf<F, S, (A) -> B>): StateT<F, S, B> =
    flatMap(MF) { a -> ff.fix().map(MF) { f -> f(a) } }

  /**
   * Map the value [A] to another [StateT] object for the same state [S] and context [F] and flatten the structure.
   *
   * @param MF [Monad] for the context [F].
   * @param fas the function to apply.
   */
  fun <B> flatMap(MF: Monad<F>, fas: (A) -> StateTOf<F, S, B>): StateT<F, S, B> =
    StateT(AndThen(runF).andThen { fsa ->
      MF.run {
        fsa.flatMap {
          fas(it.b).run(it.a)
        }
      }
    })

  /**
   * Map the value [A] to a arbitrary type [B] that is within the context of [F].
   *
   * @param MF [Monad] for the context [F].
   * @param faf the function to apply.
   */
  fun <B> flatMapF(MF: Monad<F>, faf: (A) -> Kind<F, B>): StateT<F, S, B> =
    StateT(AndThen(runF).andThen { fsa ->
      MF.run {
        fsa.flatMap { (s, a) ->
          faf(a).map { b -> Tuple2(s, b) }
        }
      }
    })

  /**
   * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
   *
   * @param FF [Functor] for the context [F].
   * @param f the function to apply.
   */
  fun <B> transform(FF: Functor<F>, f: (Tuple2<S, A>) -> Tuple2<S, B>): StateT<F, S, B> =
    StateT(AndThen(runF).andThen { fsa ->
      FF.run {
        fsa.map(f)
      }
    })

  /**
   * Combine two [StateT] objects using an instance of [SemigroupK] for [F].
   *
   * @param SF [SemigroupK] for [F].
   * @param y other [StateT] object to combine.
   */
  fun combineK(SF: SemigroupK<F>, y: StateTOf<F, S, A>): StateT<F, S, A> =
    StateT(AndThen(runF).flatMap { fa ->
      AndThen(y.fix().runF).andThen { fb ->
        SF.run {
          fa.combineK(fb)
        }
      }
    })

  /**
   * Run the stateful computation within the context `F` and get the value [A].
   *
   * @param s initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun runA(MF: Monad<F>, s: S): Kind<F, A> = MF.run {
    run(s).map { it.b }
  }

  /**
   * Run the stateful computation within the context `F` and get the state [S].
   *
   * @param s initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun runS(MF: Monad<F>, s: S): Kind<F, S> = MF.run {
    run(s).map { it.a }
  }
}

/**
 * Wrap the function with [StateT].
 */
fun <F, S, A> StateTFun<F, S, A>.stateT(): StateT<F, S, A> = StateT(this)
