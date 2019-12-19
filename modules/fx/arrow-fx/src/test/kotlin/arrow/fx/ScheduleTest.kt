package arrow.fx

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.list.foldable.forAll
import arrow.core.value
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class ScheduleTest : UnitSpec() {

  fun <S>decEqK(eqS: Eq<S>): EqK<DecisionPartialOf<S>> = object: EqK<DecisionPartialOf<S>> {
    override fun <A> Kind<DecisionPartialOf<S>, A>.eqK(other: Kind<DecisionPartialOf<S>, A>, EQ: Eq<A>): Boolean =
      (fix() to other.fix()).let { (l, r) ->
        l.cont == r.cont &&
          l.delay == r.delay &&
          eqS.run { l.state.eqv(r.state) } &&
          // don't force end result if we don't continue as it may contain Nothing
          (if (l.cont) EQ.run { l.finish.value().eqv(r.finish.value()) } else true)
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

  fun <I, A> Schedule<ForId, I, A>.runIdSchedule(i: I): Schedule.Decision<Any?, A> =
    runIdSchedule(i, 1).first()

  fun <I, A> Schedule<ForId, I, A>.runIdSchedule(i: I, n: Int): List<Schedule.Decision<Any?, A>> {
    (this as Schedule.ScheduleImpl<ForId, Any?, I, A>)
    fun go(s: Any?, rem: Int): List<Schedule.Decision<Any?, A>> =
      if (rem <= 0) emptyList()
      else update(i, s).value().let {
        listOf(it) + go(it.state, rem - 1)
      }
    return go(initialState.value(), n)
  }

  init {
    // Test laws TODO not before the larger testing rework because I don't want to rewrite this in an hour

    // find good properties to test creating schedules
    "Schedule.identity()" {
      forAll(Gen.int()) { i ->
        val dec = Schedule.identity<ForId, Int>(Id.monad()).runIdSchedule(i)

        dec.cont &&
          dec.state == Unit &&
          dec.delay.amount == 0L &&
          dec.finish.value() == i
      }
    }

    "Schedule.unfold()" {
      val dec = Schedule.unfold(Id.monad(), 0) { it + 1 }.runIdSchedule(0)

      dec.cont shouldBe true
      dec.delay.amount shouldBe 0L
      dec.state shouldBe 1
      dec.finish.value() shouldBe 1
    }

    "Schedule.forever() == Schedule.unfold(0) { it + 1 }" {
      EQK(Id.eqK(), Id.monad(), 0 as Any?).liftEq(Int.eq()).run {
        Schedule.forever(Id.monad()).eqv(Schedule.unfold(Id.monad(), 0) { it + 1 })
      }
    }

    "Schedule.recurs(n: Int)" {
      forAll(Gen.intSmall().filter { it < 1000 }) { i ->
        val res = Schedule.recurs(Id.monad(), i).runIdSchedule(0 as Any?, i + 1)

        if (i <= 0) res.isEmpty()
        else res.dropLast(1).forAll { it.cont && it.delay.amount == 0L } &&
          res.last().let { it.cont.not() && it.delay.amount == 0L && it.finish.value() == i + 1 && it.state == i + 1 }
      }
    }

    "Schedule.once() == Schedule.recurs(1)" {
      EQK(Id.eqK(), Id.monad(), 0 as Any?).liftEq(Eq.any()).run {
        Schedule.once(Id.monad()).eqv(Schedule.recurs(Id.monad(), 1).const(Unit)) shouldBe true
      }
    }

    "Schedule.never() == Schedule.recurs(0)" {
      EQK(Id.eqK(), Id.monad(), 0 as Any?).liftEq(Eq.any()).run {
        Schedule.never(Id.monad()).eqv(Schedule.recurs(Id.monad(), 0)) shouldBe true
      }
    }

    // find good properties to test running schedules
  }
}
