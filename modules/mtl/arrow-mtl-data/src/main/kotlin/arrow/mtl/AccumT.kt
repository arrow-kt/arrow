package arrow.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>` with a result in certain context `F`.
 */
typealias AccumTFun<S, F, A> = (S) -> Kind<F, Tuple2<S, A>>

/**
 * `AccumT<S, F, A>` is a stateful computation within a context `F` yielding
 * a value of type `A`. AccumT provides append-only accumulation during the computation.
 * For more general access, use `StateT` instead.
 *
 * When flatMapping the resulting state of subcomputations is combined using `Monoid<S>`
 *
 * @param F the context that wraps the stateful computation.
 * @param S the state we are performing computation upon.
 * @param A current value of computation.
 * @param accumF the stateful computation that is wrapped and managed by `AccumT`
 */
@higherkind
data class AccumT<S, F, A>(val accumT: AccumTFun<S, F, A>) : AccumTOf<S, F, A> {

  companion object {

    fun <S, F, A> just(MS: Monoid<S>, MF: Monad<F>, a: A): AccumT<S, F, A> =
      AccumT { MF.just(MS.empty() toT a) }

    operator fun <S, F, A> invoke(accumTFun: AccumTFun<S, F, A>): AccumT<S, F, A> =
      AccumT(accumTFun)

    fun <S, F, A, B> tailRecM(
      MF: Monad<F>,
      a: A,
      fa: (A) -> Kind<AccumTPartialOf<S, F>, Either<A, B>>
    ): AccumT<S, F, B> = MF.run {
      AccumT { s: S ->
        tailRecM(Tuple2(s, a)) { (s1, a0) ->
          fa(a0).fix().runAccumT(s1).map { (s2, ab) ->
            ab.bimap({ a1 -> s2 toT a1 }, { b -> s2 toT b })
          }
        }
      }
    }

    fun <F, S, A> liftF(MS: Monoid<S>, AF: Applicative<F>, fa: Kind<F, A>): AccumT<S, F, A> = AF.run {
      AccumT { fa.map { a -> Tuple2(MS.empty(), a) } }
    }

    /**
     * look is an action that fetches all the previously accumulated output.
     */
    fun <S, F> look(MS: Monoid<S>, MF: Monad<F>): AccumT<S, F, S> =
      looks(MS, MF, ::identity)

    /**
     * looks is an action that retrieves a function of the previously accumulated output.
     */
    fun <S, F, A> looks(MS: Monoid<S>, MF: Monad<F>, fs: (S) -> A): AccumT<S, F, A> =
      AccumT { s: S -> MF.just(MS.empty() toT fs(s)) }

    /**
     * add w is an action that produces the output w.
     */
    fun <S, F> add(MF: Monad<F>, s: S): AccumT<S, F, Unit> =
      AccumT { MF.just(s toT Unit) }
  }

  /**
   * Unwrap an accumulation computation.
   */
  fun runAccumT(s: S): Kind<F, Tuple2<S, A>> =
    accumT(s)

  /**
   * Extract the output from an accumulation computation.
   */
  fun execAccumT(MF: Monad<F>, s: S): Kind<F, S> =
    MF.run {
      runAccumT(s).map {
        it.a
      }
    }

  /**
   * Evaluate an accumulation computation with the given initial output history
   * and return the final value, discarding the final output.
   */
  fun evalAccumT(MF: Monad<F>, s: S): Kind<F, A> =
    MF.run {
      runAccumT(s).map {
        it.b
      }
    }

  /**
   * Map both the return value and output of a computation using the given function.
   */
  fun <G, B> mapAccumT(
    fas: (Kind<F, Tuple2<S, A>>) -> Kind<G, Tuple2<S, B>>
  ): AccumT<S, G, B> =
    AccumT(
      AndThen(accumT).andThen(fas)
    )

  fun <B> map(MF: Functor<F>, fab: (A) -> B): AccumT<S, F, B> = AccumT(
    AndThen(accumT).andThen {
      MF.run { it.map { it.a toT fab(it.b) } }
    }
  )

  fun <B> flatMap(MS: Monoid<S>, MF: Monad<F>, fa: (A) -> AccumTOf<S, F, B>): AccumT<S, F, B> =
    AccumT(
      MF.run {
        AndThen.id<S>().flatMap { s ->
          AndThen(accumT).andThen {
            it.flatMap { (s1, a) ->
              fa(a).fix().accumT(MS.run { s.combine(s1) }).flatMap { (s2, b) ->
                MF.just(MS.run { s1.combine(s2) } toT b)
              }
            }
          }
        }
      })

  fun <B> ap(MS: Monoid<S>, MF: Monad<F>, mf: AccumTOf<S, F, (A) -> B>): AccumT<S, F, B> =
    flatMap(MS, MF) { a ->
      mf.fix().map(MF) { f ->
        f(a)
      }
    }

  /**
   * Convert an accumulation (append-only) computation into a fully stateful computation.
   */
  fun toStateT(MF: Monad<F>, MS: Monoid<S>): StateT<F, S, A> =
    StateT(
      AndThen.id<S>().flatMap { s: S ->
        AndThen(accumT).andThen {
          MF.run {
            it.map { (s1, a) ->
              MS.run { s.combine(s1) } toT a
            }
          }
        }
      })
}

/**
 * Convert a read-only computation into an accumulation computation.
 */
fun <F, S, A> ReaderT<F, S, A>.toAccumT(
  FF: Functor<F>,
  MS: Monoid<S>
): AccumT<S, F, A> =
  AccumT(AndThen(run).andThen {
    FF.run {
      it.map { a ->
        MS.empty() toT a
      }
    }
  })

/**
 * Convert a writer computation into an accumulation computation.
 */
fun <F, S, A> WriterT<F, S, A>.toAccumT(): AccumT<S, F, A> = AccumT { value() }
