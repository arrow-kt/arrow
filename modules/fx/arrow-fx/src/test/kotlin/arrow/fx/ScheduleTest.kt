package arrow.fx

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.list.foldable.forAll
import arrow.core.extensions.monoid
import arrow.core.toT
import arrow.core.value
import arrow.fx.extensions.schedule.alternative.alternative
import arrow.fx.extensions.schedule.applicative.applicative
import arrow.fx.extensions.schedule.category.category
import arrow.fx.extensions.schedule.profunctor.profunctor
import arrow.fx.extensions.schedule.semiring.semiring
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.applicative
import arrow.test.generators.intSmall
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.CategoryLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.ProfunctorLaws
import arrow.test.laws.SemiringLaws
import arrow.test.laws.forFew
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.math.pow

class ScheduleTest : UnitSpec() {

  fun decEqK(): EqK<DecisionPartialOf<Any?>> = object: EqK<DecisionPartialOf<Any?>> {
    override fun <A> Kind<DecisionPartialOf<Any?>, A>.eqK(other: Kind<DecisionPartialOf<Any?>, A>, EQ: Eq<A>): Boolean =
      (fix() to other.fix()).let { (l, r) ->
        l.cont == r.cont &&
          l.delay.nanoseconds == r.delay.nanoseconds &&
          // don't force end result if we don't continue as it may contain Nothing
          (if (l.cont) EQ.run { l.finish.value().eqv(r.finish.value()) } else true)
      }
  }

  fun <F, I> EQK(fEqK: EqK<F>, MF: Monad<F>, i: I): EqK<SchedulePartialOf<F, I>> = object: EqK<SchedulePartialOf<F, I>> {
    override fun <A> Kind<SchedulePartialOf<F, I>, A>.eqK(other: Kind<SchedulePartialOf<F, I>, A>, EQ: Eq<A>): Boolean {
      val t = fix() as Schedule.ScheduleImpl<F, Any?, I, A>
      (other as Schedule.ScheduleImpl<F, Any?, I, A>)

      return MF.run {
        val lhs = t.initialState.flatMap { s -> t.update(i, s) }
        val rhs = other.initialState.flatMap { s -> other.update(i, s) }

        fEqK.liftEq(decEqK().liftEq(EQ)).run { lhs.eqv(rhs) }
      }
    }
  }

  // This is a bad gen. But generating random schedules is weird
  fun <F, I> Schedule.Companion.genK(MF: Monad<F>): GenK<SchedulePartialOf<F, I>> = object: GenK<SchedulePartialOf<F, I>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<SchedulePartialOf<F, I>, A>> =
      gen.applicative(Schedule.applicative<F, I>(MF))
  }

  fun <I, A> Schedule<ForId, I, A>.runIdSchedule(i: I): Schedule.Decision<Any?, A> =
    runIdSchedule(i, 1).first()

  fun <I, A> Schedule<ForId, I, A>.runIdSchedule(i: I, n: Int): List<Schedule.Decision<Any?, A>> {
    (this as Schedule.ScheduleImpl<ForId, Any?, I, A>)
    tailrec fun go(s: Any?, rem: Int, acc: List<Schedule.Decision<Any?, A>>): List<Schedule.Decision<Any?, A>> =
      if (rem <= 0) acc
      else {
        val res = update(i, s).value()
        go(res.state, rem - 1, acc + listOf(res))
      }
    return go(initialState.value(), n, emptyList())
  }

  val scheduleEq = EQK(Id.eqK(), Id.monad(), 0 as Any?).liftEq(Eq.any())

  init {
    // Test laws TODO not before the larger testing rework because I don't want to rewrite this in an hour
    testLaws(
      ApplicativeLaws.laws(
        Schedule.applicative<ForId, Int>(Id.monad()),
        Schedule.genK<ForId, Int>(Id.monad()),
        EQK(Id.eqK(), Id.monad(), 0)
      ),
      /* TODO uncomment when semiring laws use eqv instead of ==
      SemiringLaws.laws(
        Schedule.semiring<ForId, Any?, Int>(Int.monoid(), Id.monad()),
        Schedule.forever(Id.monad()),
        Schedule.forever(Id.monad()),
        Schedule.forever(Id.monad())
      )
      */
      // TODO alternative laws once that uses GenK + EqK
      MonoidKLaws.laws(
        Schedule.alternative<ForId, Int>(Id.monad()),
        Schedule.genK<ForId, Int>(Id.monad()),
        EQK(Id.eqK(), Id.monad(), 0)
      ),
      ProfunctorLaws.laws(
        Schedule.profunctor(),
        { i: Int -> Schedule.applicative<ForId, Int>(Id.monad()).just(i) },
        EQK(Id.eqK(), Id.monad(), 0).liftEq(Int.eq())
      ),
      CategoryLaws.laws(
        Schedule.category(Id.monad()),
        { i: Int -> Schedule.applicative<ForId, Int>(Id.monad()).just(i) },
        EQK(Id.eqK(), Id.monad(), 0).liftEq(Int.eq())
      )
    )

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
      scheduleEq.run {
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
      scheduleEq.run {
        Schedule.once(Id.monad()).eqv(Schedule.recurs(Id.monad(), 1).const(Unit)) shouldBe true
      }
    }

    "Schedule.never() == Schedule.recurs(0)" {
      scheduleEq.run {
        Schedule.never(Id.monad()).eqv(Schedule.recurs(Id.monad(), 0)) shouldBe true
      }
    }

    "Schedule.spaced()" {
      forAll(Gen.intSmall().filter { it > 0 }, Gen.intSmall().filter { it > 0 }.filter { it < 1000 }) { i, n ->
        val res = Schedule.spaced(Id.monad(), i.seconds).runIdSchedule(0, n)

        res.forAll { it.delay.nanoseconds == i.seconds.nanoseconds && it.cont }
      }
    }

    "Schedule.fibonacci()" {
      forFew(5, Gen.intSmall().filter { it > 0 }.filter { it < 10 }, Gen.intSmall().filter { it > 0 }.filter { it < 10 }) { i, n ->
        val res = Schedule.fibonacci(Id.monad(), i.seconds).runIdSchedule(0, n)

        val sum = res.fold(0L) { acc, v ->
          acc + v.delay.amount
        }
        val fibSum = fibs(i.toLong()).drop(1).take(res.size).sum()

        res.forAll { it.cont } && sum == fibSum
      }
    }

    "Schedule.linear()" {
      forFew(5, Gen.intSmall().filter { it > 0 }.filter { it < 10 }, Gen.intSmall().filter { it > 0 }.filter { it < 10 }) { i, n ->
        val res = Schedule.linear(Id.monad(), i.seconds).runIdSchedule(0, n)

        val sum = res.fold(0L) { acc, v -> acc + v.delay.amount }
        val expSum = linear(i.toLong()).drop(1).take(n).sum()

        res.forAll { it.cont } && sum == expSum
      }
    }

    "Schedule.exponential()" {
      forFew(5, Gen.intSmall().filter { it > 0 }.filter { it < 10 }, Gen.intSmall().filter { it > 0 }.filter { it < 10 }) { i, n ->
        val res = Schedule.exponential(Id.monad(), i.seconds).runIdSchedule(0, n)

        val sum = res.fold(0L) { acc, v -> acc + v.delay.amount }
        val expSum = exp(i.toLong()).drop(1).take(n).sum()

        res.forAll { it.cont } && sum == expSum
      }
    }

    // find good properties to test running schedules
  }

  private fun fibs(one: Long): Sequence<Long> = generateSequence(0L toT one) { (a, b) ->
    b toT (a + b)
  }.map { it.a }

  private fun exp(base: Long): Sequence<Long> = generateSequence(base toT 1.0) { (_, n) ->
    (base * 2.0.pow(n)).toLong() toT n + 1
  }.map { it.a }

  private fun linear(base: Long): Sequence<Long> = generateSequence(base toT 1L) { (_, n) ->
    (base * n) toT (n + 1)
  }.map { it.a }
}
