package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.SemigroupK

/**
 * Alias that represent stateful computation of the form `(SA) -> Tuple2<SB, A>` with a result in certain context `F`.
 */
typealias IndexedStateTFun<F, SA, SB, A> = (SA) -> Kind<F, Tuple2<SB, A>>

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
typealias IndexedStateTFunOf<F, SA, SB, A> = Kind<F, IndexedStateTFun<F, SA, SB, A>>

/**
 * Run the stateful computation within the context `F`.
 *
 * @param MF [Monad] for the context [F]
 * @param initial state to run stateful computation
 */
fun <F, SA, SB, A> IndexedStateTOf<F, SA, SB, A>.runM(MF: Monad<F>, initial: SA): Kind<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(MF, initial)

/**
 * `IndexedStateT<F, SA, SB, A>` is a stateful computation within a context `F` yielding  a value of type `A`.
 * i.e. IndexedStateT<EitherPartialKind<E>, SA, SB, A> = Either<E, IndexedState<SA, SB, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param SA the state we are preforming computation upon.
 * @param SB the resulting state of the computation
 * @param A current value of computation.
 * @param runF the stateful computation that is wrapped and managed by `StateT`
 */
@higherkind
class IndexedStateT<F, SA, SB, A>(
  val runF: Kind<F, (SA) -> Kind<F, Tuple2<SB, A>>>
) : IndexedStateTOf<F, SA, SB, A> {

  companion object {

    /**
     * Constructor to create `StateT<F, S, A>` given a [StateTFun].
     *
     * @param AF [Applicative] for the context [F].
     * @param run the stateful function to wrap with [StateT].
     */
    operator fun <F, SA, SB, A> invoke(AF: Applicative<F>, run: IndexedStateTFun<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(AF.just(run))

    /**
     * Alias for constructor [StateT].
     *
     * @param runF the function to wrap within [StateT].
     */
    fun <F, SA, SB, A> invokeF(runF: IndexedStateTFunOf<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(runF)

    /**
     * Lift a value of type `A` into `IndexedStateT<F, S, S, A>`.
     *
     * @param AF [Applicative] for the context [F].
     * @param fa the value to lift.
     */
    fun <F, S, A> lift(AF: Applicative<F>, fa: Kind<F, A>): IndexedStateT<F, S, S, A> = AF.run {
      IndexedStateT(AF.just({ s -> fa.map { a -> Tuple2(s, a) } }))
    }

    /**
     * Return input without modifying it.
     *
     * @param AF [Applicative] for the context [F].
     */
    fun <F, S> get(AF: Applicative<F>): IndexedStateT<F, S, S, S> = IndexedStateT(AF.just({ s -> AF.just(Tuple2(s, s)) }))

    /**
     * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
     *
     * @param AF [Applicative] for the context [F].
     * @param f the function applied to extract [T] from [S].
     */
    fun <F, S, T> inspect(AF: Applicative<F>, f: (S) -> T): IndexedStateT<F, S, S, T> = IndexedStateT(AF.just({ s -> AF.just(Tuple2(s, f(s))) }))

    /**
     * Modify the state with [f] `(S) -> S` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param f the modify function to apply.
     */
    fun <F, S> modify(AF: Applicative<F>, f: (S) -> S): IndexedStateT<F, S, S, Unit> = AF.run {
      IndexedStateT(just({ s -> just(f(s)).map { it toT Unit } }))
    }

    /**
     * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param f the modify function to apply.
     */
    fun <F, S> modifyF(AF: Applicative<F>, f: (S) -> Kind<F, S>): IndexedStateT<F, S, S, Unit> = AF.run {
      IndexedStateT(just({ s -> f(s).map { it toT Unit } }))
    }

    /**
     * Set the state to a value [s] and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param s value to set.
     */
    fun <F, SA, SB> set(AF: Applicative<F>, s: SB): IndexedStateT<F, SA, SB, Unit> = IndexedStateT(AF.just({ _ -> AF.just(Tuple2(s, Unit)) }))

    /**
     * Set the state to a value [s] of type `HK<F, S>` and return [Unit].
     *
     * @param AF [Applicative] for the context [F].
     * @param s value to set.
     */
    fun <F, SA, SB> setF(AF: Applicative<F>, s: Kind<F, SB>): IndexedStateT<F, SA, SB, Unit> = AF.run {
      IndexedStateT(just({ _ -> s.map { Tuple2(it, Unit) } }))
    }

    /**
     * Construct a [IndexedStateT] with a current value [a].
     *
     * @param AF [Applicative] for the context [F].
     * @param a current value of the state.
     */
    fun <F, S, A> pure(AF: Applicative<F>, a: A): IndexedStateT<F, S, S, A> = IndexedStateT(AF.just({ s: S -> AF.just(Tuple2(s, a)) }))

    /**
     * Tail recursive function that keeps calling [f]  until [arrow.core.Either.Right] is returned.
     *
     * @param a initial value to start running recursive call to [f]
     * @param f function that is called recursively until [arrow.core.Either.Right] is returned.
     * @param MF [Monad] for the context [F].
     */
    fun <F, S, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> IndexedStateTOf<F, S, S, Either<A, B>>): IndexedStateT<F, S, S, B> =
      IndexedStateT(MF, run = { s ->
        MF.tailRecM(Tuple2(s, a)) { (s, a) ->
          MF.run {
            f(a).fix().run(MF, s).map { (s, ab) ->
              ab.bimap({ s toT it }, { s toT it })
            }
          }
        }
      })

  }

  /**
   * Map current value [A] to [B] given a function [f].
   *
   * @param f the function to apply.
   * @param FF [Functor] for the context [F].
   */
  fun <B> map(FF: Functor<F>, f: (A) -> B): IndexedStateT<F, SA, SB, B> = transform(FF) { (s, a) -> Tuple2(s, f(a)) }

  /**
   * Combine with another [StateT] of same context [F] and state [S].
   *
   * @param sb other state with value of type `B`.
   * @param fn the function to apply.
   * @param MF [Monad] for the context [F].
   */
  fun <B, SC, Z> map2(MF: Monad<F>, sb: IndexedStateTOf<F, SB, SC, B>, fn: (A, B) -> Z): IndexedStateT<F, SA, SC, Z> = MF.run {
    invokeF(MF.map(runF, sb.fix().runF) { (ssa, ssb) ->
      ssa.andThen { fsa ->
        fsa.flatMap { (s, a) ->
          ssb(s).map { (s, b) -> Tuple2(s, fn(a, b)) }
        }
      }
    })
  }

  /**
   * Controlled combination of [IndexedStateT] that is of same context [F] and state [S] using [Eval].
   *
   * @param sb other state with value of type `B`.
   * @param fn the function to apply.
   * @param MF [Monad] for the context [F].
   */
  fun <B, SC, Z> map2Eval(MF: Monad<F>, sb: Eval<IndexedStateTOf<F, SB, SC, B>>, fn: (A, B) -> Z): Eval<IndexedStateT<F, SA, SC, Z>> = MF.run {
    sb.map {
      MF.map(runF, it.fix().runF) { (ssa, ssb) ->
        ssa.andThen { fsa ->
          fsa.flatMap { (s, a) ->
            ssb((s)).map { (s, b) -> Tuple2(s, fn(a, b)) }
          }
        }
      }
    }.map(IndexedStateT.Companion::invokeF)
  }

  /**
   * Apply a function `(S) -> B` that operates within the [IndexedStateT] context.
   *
   * @param ff function with the [IndexedStateT] context.
   * @param MF [Monad] for the context [F].
   */
  fun <B, SC> ap(MF: Monad<F>, ff: IndexedStateTOf<F, SB, SC, (A) -> B>): IndexedStateT<F, SA, SC, B> =
    map2(MF, ff) { a, f -> f(a) }

  /**
   * Create a product of the value types of [IndexedStateT].
   *
   * @param sb other stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun <B, SC> product(MF: Monad<F>, sb: IndexedStateTOf<F, SB, SC, B>): IndexedStateT<F, SA, SC, Tuple2<A, B>> = map2(MF, sb.fix()) { a, b -> Tuple2(a, b) }

  /**
   * Map the value [A] to another [IndexedStateT] object for the same state [S] and context [F] and flatten the structure.
   *
   * @param fas the function to apply.
   * @param MF [Monad] for the context [F].
   */
  fun <B, SC> flatMap(MF: Monad<F>, fas: (A) -> IndexedStateTOf<F, SB, SC, B>): IndexedStateT<F, SA, SC, B> = MF.run {
    invokeF(
      runF.map { safsba ->
        safsba.andThen { fsba ->
          fsba.flatMap {
            fas(it.b).runM(MF, it.a)
          }
        }
      })
  }

  /**
   * Map the value [A] to a arbitrary type [B] that is within the context of [F].
   *
   * @param faf the function to apply.
   * @param MF [Monad] for the context [F].
   */
  fun <B> flatMapF(MF: Monad<F>, faf: (A) -> Kind<F, B>): IndexedStateT<F, SA, SB, B> = MF.run {
    invokeF(runF.map { sfsa ->
      sfsa.andThen { fsa ->
        fsa.flatMap { (s, a) ->
          faf(a).map {
            s toT it
          }
        }
      }
    })
  }

  fun <X> imap(FF: Functor<F>, f: (SB) -> X): IndexedStateT<F, SA, X, A> = bimap(FF, f, ::identity)

  /**
   * Bimap the value [A] and the state [SB].
   *
   * @param FF [Functor] for the context [F].
   * @param f function to map state [SB] to [SC].
   * @param g function to map value [A] to [B].
   */
  fun <B, SC> bimap(FF: Functor<F>, f: (SB) -> SC, g: (A) -> B): IndexedStateT<F, SA, SC, B> =
    transform(FF) { (sb, a) -> f(sb) toT g(a) }

  /**
   * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
   *
   * @param f the function to apply.
   * @param FF [Functor] for the context [F].
   */
  fun <B, SC> transform(FF: Functor<F>, f: (Tuple2<SB, A>) -> Tuple2<SC, B>): IndexedStateT<F, SA, SC, B> = FF.run {
    invokeF(runF.map { sfsa ->
      sfsa.andThen { fsa ->
        fsa.map { (s, a) -> f(s toT a) }
      }
    })
  }

  /**
   * Like [transform], but allows the context to change from [F] to [G].
   *
   * @param MF [Monad] for the context [F].
   * @param AG [Applicative] for the context [F].
   * @param f function to transform state within context [F] to state in context [G].
   */
  fun <G, B, SC> transformF(MF: Monad<F>, AG: Applicative<G>, f: (Kind<F, Tuple2<SB, A>>) -> Kind<G, Tuple2<SC, B>>): IndexedStateT<G, SA, SC, B> = IndexedStateT(AG, run = { s ->
    f(run(MF, s))
  })

  /**
   * Transform the state used to an arbitrary type [R].
   *
   * @param FF [Functor] for the context [F].
   * @param f function that can extract state [SA] from [R].
   * @param g function that can calculate new state [R].
   */
  fun <R> transformS(FF: Functor<F>, f: (R) -> SA, g: (Tuple2<R, SB>) -> R): IndexedStateT<F, R, R, A> = FF.run {
    IndexedStateT.invokeF(runF.map { sfsa ->
      { r: R ->
        val sa = f(r)
        val fsba = sfsa(sa)
        fsba.map { (sb, a) ->
          g(Tuple2(r, sb)) toT a
        }
      }
    })
  }

  /**
   * Modify the state [SB].
   *
   * @param FF [Functor] for the context [F].
   * @param f function to modify state [SB] to [SC].
   */
  fun <SC> modify(FF: Functor<F>, f: (SB) -> SC): IndexedStateT<F, SA, SC, A> = transform(FF) { (sb, a) ->
    Tuple2(f(sb), a)
  }

  /**
   * Inspect a value from the input state, without modifying the state.
   *
   * @param FF [Functor] for the context [F].
   * @param f function to inspect value from the state [SB].
   */
  fun <B> inspect(FF: Functor<F>, f: (SB) -> B): IndexedStateT<F, SA, SB, B> = transform(FF) { (sb, _) ->
    Tuple2(sb, f(sb))
  }

  /**
   * Get the input state, without modifying the state.
   *
   * @param FF [Functor] for the context [F].
   */
  fun get(FF: Functor<F>): IndexedStateT<F, SA, SB, SB> = inspect(FF, ::identity)

  /**
   * Combine two [IndexedStateT] objects using an instance of [SemigroupK] for [F].
   *
   * @param y other [IndexedStateT] object to combine.
   * @param MF [Monad] for the context [F].
   * @param SF [SemigroupK] for [F].
   */
  fun combineK(y: IndexedStateTOf<F, SA, SB, A>, MF: Monad<F>, SF: SemigroupK<F>): IndexedStateT<F, SA, SB, A> = SF.run {
    IndexedStateT(MF.just({ s -> run(MF, s).combineK(y.fix().run(MF, s)) }))
  }

  /**
   * Run the stateful computation within the context `F`.
   *
   * @param initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun run(MF: Monad<F>, initial: SA): Kind<F, Tuple2<SB, A>> = MF.run {
    runF.flatMap { f -> f(initial) }
  }

  /**
   * Run the stateful computation within the context `F` and get the value [A].
   *
   * @param s initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun runA(MF: Monad<F>, s: SA): Kind<F, A> = MF.run {
    run(MF, s).map { it.b }
  }

  /**
   * Run the stateful computation within the context `F` and get the state [S].
   *
   * @param s initial state to run stateful computation.
   * @param MF [Monad] for the context [F].
   */
  fun runS(MF: Monad<F>, s: SA): Kind<F, SB> = MF.run {
    run(MF, s).map { it.a }
  }

}
