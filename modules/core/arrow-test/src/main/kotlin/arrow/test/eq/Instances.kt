package arrow.test.eq

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.waitFor
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.mtl.AccumT
import arrow.mtl.AccumTPartialOf
import arrow.mtl.Kleisli
import arrow.mtl.KleisliPartialOf
import arrow.mtl.StateT
import arrow.mtl.StateTPartialOf
import arrow.mtl.fix
import arrow.mtl.run
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad

/**
 * This is mainly useful for testing laws because those take a EqK without any further constraints, however when testing outside of those constraints
 *  one should always generate random initial states to get a better coverage!
 */
fun <F, D> Kleisli.Companion.eqK(eqKF: EqK<F>, ctx: D): EqK<KleisliPartialOf<F, D>> = object : EqK<KleisliPartialOf<F, D>> {
  override fun <A> Kind<KleisliPartialOf<F, D>, A>.eqK(other: Kind<KleisliPartialOf<F, D>, A>, EQ: Eq<A>): Boolean =
    eqKF.liftEq(EQ).run { run(ctx).eqv(other.run(ctx)) }
}

/**
 * This is mainly useful for testing laws because those take a EqK without any further constraints, however when testing outside of those constraints
 *  one should always generate random initial states to get a better coverage!
 */
fun <F, S> StateT.Companion.eqK(EQKF: EqK<F>, EQS: Eq<S>, M: Monad<F>, s: S) = object : EqK<StateTPartialOf<F, S>> {
  override fun <A> Kind<StateTPartialOf<F, S>, A>.eqK(other: Kind<StateTPartialOf<F, S>, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      val ls = it.first.run(s)
      val rs = it.second.run(s)

      EQKF.liftEq(Tuple2.eq(EQS, EQ)).run {
        ls.eqv(rs)
      }
    }
}

fun IO.Companion.eqK(timeout: Duration = 60.seconds) = object : EqK<ForIO> {
  override fun <A> Kind<ForIO, A>.eqK(other: Kind<ForIO, A>, EQ: Eq<A>): Boolean =
    Either.eq(Eq.any(), EQ).run {
      IO.applicative().mapN(fix().attempt(), other.fix().attempt()) { (a, b) -> a.eqv(b) }
        .waitFor(timeout)
        .unsafeRunSync()
    }
}

fun <S, F> AccumT.Companion.eqK(MF: Monad<F>, eqkF: EqK<F>, eqS: Eq<S>, s: S) =
  object : EqK<AccumTPartialOf<S, F>> {
    override fun <A> Kind<AccumTPartialOf<S, F>, A>.eqK(other: Kind<AccumTPartialOf<S, F>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        it.first.runAccumT(s) to it.second.runAccumT(s)
      }.let {
        eqkF.liftEq(Tuple2.eq(eqS, EQ)).run {
          it.first.eqv(it.second)
        }
      }
  }
