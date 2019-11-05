package arrow.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.EvalOf
import arrow.core.Eval
import arrow.core.fix
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.SemigroupK

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>` with a result in certain context `F`.
 */
typealias StateTFun<S, F, A> = (S) -> Kind<F, Tuple2<S, A>>

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
typealias StateTFunOf<S, F, A> = Kind<F, StateTFun<S, F, A>>

/**
 * Run the stateful computation within the context `F`.
 *
 * @param MF [Monad] for the context [F]
 * @param s initial state to run stateful computation
 */
fun <S, F, A> StateTOf<S, F, A>.runM(MF: Monad<F>, initial: S): Kind<F, Tuple2<S, A>> = fix().run(MF, initial)

/**
 * `StateT<S, F, A>` is a stateful computation within a context `F` yielding
 * a value of type `A`. i.e. StateT<S, EitherPartialOf<E>, A> = Either<E, State<S, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param S the state we are preforming computation upon.
 * @param A current value of computation.
 * @param runF the stateful computation that is wrapped and managed by `StateT`
 */
@higherkind
class StateT<S, F, A>(
  val runF: StateTFunOf<S, F, A>
) : StateTOf<S, F, A>, StateTKindedJ<S, F, A> {

  companion object {

    fun <S, F, T> just(AF: Applicative<F>, t: T): StateT<S, F, T> =
      StateT(AF) { s -> AF.just(s toT t) }

    /**
     * Constructor to create `StateT<S, F, A>` given a [StateTFun].
     *
     * @param AF [Applicative] for the context [F].
     * @param run the stateful function to wrap with [StateT].
     */
    operator fun <S, F, A> invoke(AF: Applicative<F>, run: StateTFun<S, F, A>): StateT<S, F, A> = AF.run {
      StateT(just(run))
    }

    /**
     * Alias for constructor [StateT].
     *
     * @param runF the function to wrap within [StateT].
     */
    fun <S, F, A> invokeF(runF: StateTFunOf<S, F, A>): StateT<S, F, A> =
      StateT(runF)

    /**
     * Lift a value of type `Kind<F, A>` into `StateT<S, F, A>`.
     *
     * @param AF [Applicative] for the context [F].
     * @param fa the value to liftF.
     */
    fun <S, F, A> liftF(AF: Applicative<F>, fa: Kind<F, A>): StateT<S, F, A> = AF.run {
      StateT(just { s -> fa.map { a -> Tuple2(s, a) } })
    }

    /**
     * Return input without modifying it.
     *
     * @param AF [Applicative] for the context [F].
     */
    fun <S, F> get(AF: Applicative<F>): StateT<S, F, S> =
      StateT(AF.just { s -> AF.just(Tuple2(s, s)) })

    /**
     * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
     *
     *
     *
     * @param AF [Applicative] for the context [F].
     * @param f the function applied to inspect [T] from [S].
     */
    fun <S, F, T> inspect(AF: Applicative<F>, f: (S) -> T): StateT<S, F, T> =
      StateT(AF.just { s -> AF.just(Tuple2(s, f(s))) })

    /**
     * Modify the state with [f] `(S) -> S` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param f the modify function to apply.
     */
    fun <S, F> modify(AF: Applicative<F>, f: (S) -> S): StateT<S, F, Unit> = AF.run {
      StateT(just { s ->
        just(f(s)).map { Tuple2(it, Unit) }
      })
    }

    /**
     * Modify the state with an [Applicative] function [f] `(S) -> Kind<F, S>` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param f the modify function to apply.
     */
    fun <S, F> modifyF(AF: Applicative<F>, f: (S) -> Kind<F, S>): StateT<S, F, Unit> =
      StateT(AF.just { s -> AF.run { f(s).map { Tuple2(it, Unit) } } })

    /**
     * Set the state to a value [s] and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param s value to set.
     */
    fun <S, F> set(AF: Applicative<F>, s: S): StateT<S, F, Unit> =
      StateT(AF.just { _ -> AF.just(Tuple2(s, Unit)) })

    /**
     * Set the state to a value [s] of type `Kind<F, S>` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param s value to set.
     */
    fun <S, F> setF(AF: Applicative<F>, s: Kind<F, S>): StateT<S, F, Unit> =
      StateT(AF.just { _ -> AF.run { s.map { Tuple2(it, Unit) } } })

    /**
     * Tail recursive function that keeps calling [f]  until [arrow.Either.Right] is returned.
     *
     * @param MF [Monad] for the context [F].
     * @param a initial value to start running recursive call to [f]
     * @param f function that is called recusively until [arrow.Either.Right] is returned.
     */
    fun <S, F, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> StateTOf<S, F, Either<A, B>>): StateT<S, F, B> = MF.run {
      StateT(just { s: S ->
        tailRecM(Tuple2(s, a)) { (s, a0) ->
          f(a0).runM(this, s).map { (s, ab) ->
            ab.bimap({ a1 -> Tuple2(s, a1) }, { b -> Tuple2(s, b) })
          }
        }
      }
      )
    }
  }

  /**
   * Map current value [A] given a function [f].
   *
   * @param FF [Functor] for the context [F].
   * @param f the function to apply.
   */
  fun <B> map(FF: Functor<F>, f: (A) -> B): StateT<S, F, B> = transform(FF) { (s, a) -> Tuple2(s, f(a)) }

  /**
   * Combine with another [StateT] of same context [F] and state [S].
   *
   * @param MF [Monad] for the context [F].
   * @param sb other state with value of type `B`.
   * @param fn the function to apply.
   */
  fun <B, Z> map2(MF: Monad<F>, sb: StateTOf<S, F, B>, fn: (A, B) -> Z): StateT<S, F, Z> =
    MF.run {
      invokeF(runF.map2(sb.fix().runF) { (ssa, ssb) ->
        AndThen(ssa).andThen { fsa ->
          fsa.flatMap { (s, a) ->
            ssb(s).map { (s, b) -> Tuple2(s, fn(a, b)) }
          }
        }
      })
    }

  /**
   * Controlled combination of [StateT] that is of same context [F] and state [S] using [Eval].
   *
   * @param MF [Monad] for the context [F].
   * @param sb other state with value of type `B`.
   * @param fn the function to apply.
   */
  fun <B, Z> map2Eval(MF: Monad<F>, sb: EvalOf<StateT<S, F, B>>, fn: (A, B) -> Z): Eval<StateT<S, F, Z>> = MF.run {
    runF.map2Eval(sb.fix().map { it.runF }) { (ssa, ssb) ->
      AndThen(ssa).andThen { fsa ->
        fsa.flatMap { (s, a) ->
          ssb((s)).map { (s, b) -> Tuple2(s, fn(a, b)) }
        }
      }
    }.map { invokeF(it) }
  }

  /**
   * Apply a function `(S) -> B` that operates within the [StateT] context.
   *
   * @param MF [Monad] for the context [F].
   * @param ff function with the [StateT] context.
   */
  fun <B> ap(MF: Monad<F>, ff: StateTOf<S, F, (A) -> B>): StateT<S, F, B> =
    ff.fix().map2(MF, this) { f: (A) -> B, a: A -> f(a) }

  /**
   * Create a product of the value types of [StateT].
   *
   * @param MF [Monad] for the context [F].
   * @param sb other stateful computation.
   */
  fun <B> product(MF: Monad<F>, sb: StateTOf<S, F, B>): StateT<S, F, Tuple2<A, B>> =
    map2(MF, sb) { a, b -> Tuple2(a, b) }

  /**
   * Map the value [A] to another [StateT] object for the same state [S] and context [F] and flatten the structure.
   *
   * @param MF [Monad] for the context [F].
   * @param fas the function to apply.
   */
  fun <B> flatMap(MF: Monad<F>, fas: (A) -> StateTOf<S, F, B>): StateT<S, F, B> = MF.run {
    invokeF(
      runF.map { sfsa ->
        AndThen(sfsa).andThen { fsa ->
          fsa.flatMap {
            fas(it.b).runM(MF, it.a)
          }
        }
      })
  }

  /**
   * Map the value [A] to a arbitrary type [B] that is within the context of [F].
   *
   * @param MF [Monad] for the context [F].
   * @param faf the function to apply.
   */
  fun <B> flatMapF(MF: Monad<F>, faf: (A) -> Kind<F, B>): StateT<S, F, B> = MF.run {
    invokeF(
      runF.map { sfsa ->
        AndThen(sfsa).andThen { fsa ->
          fsa.flatMap { (s, a) ->
            faf(a).map { b -> Tuple2(s, b) }
          }
        }
      })
  }

  /**
   * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
   *
   * @param FF [Functor] for the context [F].
   * @param f the function to apply.
   */
  fun <B> transform(FF: Functor<F>, f: (Tuple2<S, A>) -> Tuple2<S, B>): StateT<S, F, B> = FF.run {
    invokeF(
      runF.map { sfsa ->
        sfsa.andThen { fsa ->
          fsa.map(f)
        }
      })
  }

  /**
   * Combine two [StateT] objects using an instance of [SemigroupK] for [F].
   *
   * @param MF [Monad] for the context [F].
   * @param SF [SemigroupK] for [F].
   * @param y other [StateT] object to combine.
   */
  fun combineK(MF: Monad<F>, SF: SemigroupK<F>, y: StateTOf<S, F, A>): StateT<S, F, A> = SF.run {
    StateT(MF.just { s -> run(MF, s).combineK(y.fix().run(MF, s)) })
  }

  /**
   * Run the stateful computation within the context `F`.
   *
   * @param initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun run(MF: Monad<F>, initial: S): Kind<F, Tuple2<S, A>> = MF.run {
    runF.flatMap { f -> f(initial) }
  }

  /**
   * Run the stateful computation within the context `F` and get the value [A].
   *
   * @param s initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun runA(MF: Monad<F>, s: S): Kind<F, A> = MF.run {
    run(MF, s).map { it.b }
  }

  /**
   * Run the stateful computation within the context `F` and get the state [S].
   *
   * @param s initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun runS(MF: Monad<F>, s: S): Kind<F, S> = MF.run {
    run(MF, s).map { it.a }
  }
}

/**
 * Wrap the function with [StateT].
 *
 * @param MF [Monad] for the context [F].
 */
fun <S, F, A> StateTFunOf<S, F, A>.stateT(): StateT<S, F, A> = StateT(this)

/**
 * Wrap the function with [StateT].
 *
 * @param MF [Monad] for the context [F].
 */
fun <S, F, A> StateTFun<S, F, A>.stateT(MF: Monad<F>): StateT<S, F, A> = StateT(MF, this)
