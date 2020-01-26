package arrow.test.eq

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.mtl.Kleisli
import arrow.mtl.KleisliPartialOf
import arrow.mtl.StateT
import arrow.mtl.StateTPartialOf
import arrow.mtl.fix
import arrow.mtl.run
import arrow.mtl.runM
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
      val ls = it.first.runM(M, s)
      val rs = it.second.runM(M, s)

      EQKF.liftEq(Tuple2.eq(EQS, EQ)).run {
        ls.eqv(rs)
      }
    }
}

fun IO.Companion.eqK(timeout: Duration = 60.seconds) = object : EqK<ForIO> {
  override fun <A> Kind<ForIO, A>.eqK(other: Kind<ForIO, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      val ls = it.first.attempt().unsafeRunTimed(timeout)
      val rs = it.second.attempt().unsafeRunTimed(timeout)

      Option.eq(Either.eq(Eq.any(), EQ)).run {
        ls.eqv(rs)
      }
    }
}
