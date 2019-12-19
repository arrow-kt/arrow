package arrow.fx

import arrow.Kind
import arrow.test.UnitSpec
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad

class ScheduleTest : UnitSpec() {

  fun <S>decEqK(eqS: Eq<S>): EqK<DecisionPartialOf<S>> = object: EqK<DecisionPartialOf<S>> {
    override fun <A> Kind<DecisionPartialOf<S>, A>.eqK(other: Kind<DecisionPartialOf<S>, A>, EQ: Eq<A>): Boolean =
      (fix() to other.fix()).let { (l, r) ->
        l.cont == r.cont &&
          l.delay == r.delay &&
          EQ.run { l.finish.value().eqv(r.finish.value()) } &&
          eqS.run { l.state.eqv(r.state) }
      }
  }

  fun <F, I> EQK(fEqK: EqK<F>, MF: Monad<F>, i: I): EqK<SchedulePartialOf<F, I>> = object: EqK<SchedulePartialOf<F, I>> {
    override fun <A> Kind<SchedulePartialOf<F, I>, A>.eqK(other: Kind<SchedulePartialOf<F, I>, A>, EQ: Eq<A>): Boolean {
      val t = fix() as Schedule.ScheduleImpl<F, Any?, I, A>
      (other as Schedule.ScheduleImpl<F, Any?, I, A>)

      val initialStateEq = fEqK.liftEq(Eq.any()).run { t.initialState.eqv(other.initialState) }
      val updateEq = MF.run {
        val lhs = t.initialState.flatMap { s -> t.update(i, s) }
        val rhs = other.initialState.flatMap { s -> other.update(i, s) }

        fEqK.liftEq(decEqK(Eq.any()).liftEq(EQ)).run { lhs.eqv(rhs) }
      }

      return initialStateEq && updateEq
    }
  }

  init {
    // Test laws

    // find good properties to test creating schedules

    // find good properties to test running schedules
  }
}
