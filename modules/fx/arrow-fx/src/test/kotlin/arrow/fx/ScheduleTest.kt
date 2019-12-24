package arrow.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.list.foldable.forAll
import arrow.core.extensions.monoid
import arrow.core.toT
import arrow.core.value
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.monad.monad
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.extensions.io.monadThrow.monadThrow
import arrow.fx.extensions.schedule.alternative.alternative
import arrow.fx.extensions.schedule.applicative.applicative
import arrow.fx.extensions.schedule.category.category
import arrow.fx.extensions.schedule.profunctor.profunctor
import arrow.fx.extensions.schedule.semiring.semiring
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.nanoseconds
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.generators.GenK
import arrow.test.generators.applicative
import arrow.test.generators.intSmall
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.CategoryLaws
import arrow.test.laws.ProfunctorLaws
import arrow.test.laws.SemiringLaws
import arrow.test.laws.forFew
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.math.max
import kotlin.math.pow

class ScheduleTest : UnitSpec() {

  fun decEqK(): EqK<DecisionPartialOf<Any?>> = object : EqK<DecisionPartialOf<Any?>> {
    override fun <A> Kind<DecisionPartialOf<Any?>, A>.eqK(other: Kind<DecisionPartialOf<Any?>, A>, EQ: Eq<A>): Boolean =
      (fix() to other.fix()).let { (l, r) ->
        l.cont == r.cont &&
          l.delay.nanoseconds == r.delay.nanoseconds &&
          // don't force end result if we don't continue as it may contain Nothing
          (if (l.cont) EQ.run { l.finish.value().eqv(r.finish.value()) } else true)
      }
  }

  fun <F, I> EQK(fEqK: EqK<F>, MF: Monad<F>, i: I): EqK<SchedulePartialOf<F, I>> = object : EqK<SchedulePartialOf<F, I>> {
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
  fun <F, I> Schedule.Companion.genK(MF: Monad<F>): GenK<SchedulePartialOf<F, I>> = object : GenK<SchedulePartialOf<F, I>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<SchedulePartialOf<F, I>, A>> =
      gen.applicative(Schedule.applicative<F, I>(MF))
  }

  fun Schedule.Decision.Companion.genK(): GenK<DecisionPartialOf<Any?>> = object : GenK<DecisionPartialOf<Any?>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<DecisionPartialOf<Any?>, A>> =
      Gen.bind(
        Gen.bool(),
        Gen.intSmall(),
        gen
      ) { cont, delay, res -> Schedule.Decision(cont, delay.nanoseconds, 0 as Any?, Eval.now(res)) }
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

  fun refTimer(ref: Ref<ForIO, Duration>): Timer<ForIO> = object : Timer<ForIO> {
    override fun sleep(duration: Duration): Kind<ForIO, Unit> =
      ref.update { d -> d + duration }
  }

  val scheduleEq = EQK(Id.eqK(), Id.monad(), 0 as Any?).liftEq(Eq.any())

  init {
    testLaws(
      ApplicativeLaws.laws(
        Schedule.applicative<ForId, Int>(Id.monad()),
        Schedule.genK<ForId, Int>(Id.monad()),
        EQK(Id.eqK(), Id.monad(), 0)
      ),
      SemiringLaws.laws(
        Schedule.semiring<ForId, Any?, Int>(Int.monoid(), Id.monad()),
        Schedule.forever(Id.monad()),
        Schedule.forever(Id.monad()),
        Schedule.forever(Id.monad()),
        EQK(Id.eqK(), Id.monad(), 0 as Any?).liftEq(Int.eq())
      ),
      AlternativeLaws.laws(
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
      val dec = Schedule.unfold<ForId, Any?, Int>(Id.monad(), 0) { it + 1 }.runIdSchedule(0)

      dec.cont shouldBe true
      dec.delay.amount shouldBe 0L
      dec.state shouldBe 1
      dec.finish.value() shouldBe 1
    }

    "Schedule.forever() == Schedule.unfold(0) { it + 1 }" {
      scheduleEq.run {
        Schedule.forever<ForId, Any?>(Id.monad()).eqv(Schedule.unfold(Id.monad(), 0) { it + 1 })
      }
    }

    "Schedule.recurs(n: Int)" {
      forAll(Gen.intSmall().filter { it < 1000 }) { i ->
        val res = Schedule.recurs<ForId, Int>(Id.monad(), i).runIdSchedule(0, i + 1)

        if (i <= 0) res.isEmpty()
        else res.dropLast(1).forAll { it.cont && it.delay.amount == 0L } &&
          res.last().let { it.cont.not() && it.delay.amount == 0L && it.finish.value() == i + 1 && it.state == i + 1 }
      }
    }

    "Schedule.once() == Schedule.recurs(1)" {
      scheduleEq.run {
        Schedule.once<ForId, Any?>(Id.monad()).eqv(Schedule.recurs<ForId, Any?>(Id.monad(), 1).const(Unit)) shouldBe true
      }
    }

    "Schedule.never() == Schedule.recurs(0)" {
      scheduleEq.run {
        Schedule.never<ForId, Any?>(Id.monad()).eqv(Schedule.recurs(Id.monad(), 0)) shouldBe true
      }
    }

    "Schedule.spaced()" {
      forAll(Gen.intSmall().filter { it > 0 }, Gen.intSmall().filter { it > 0 }.filter { it < 1000 }) { i, n ->
        val res = Schedule.spaced<ForId, Any>(Id.monad(), i.seconds).runIdSchedule(0, n)

        res.forAll { it.delay.nanoseconds == i.seconds.nanoseconds && it.cont }
      }
    }

    "Schedule.fibonacci()" {
      forFew(5, Gen.intSmall().filter { it > 0 }.filter { it < 10 }, Gen.intSmall().filter { it > 0 }.filter { it < 10 }) { i, n ->
        val res = Schedule.fibonacci<ForId, Any?>(Id.monad(), i.seconds).runIdSchedule(0, n)

        val sum = res.fold(0L) { acc, v ->
          acc + v.delay.amount
        }
        val fibSum = fibs(i.toLong()).drop(1).take(res.size).sum()

        res.forAll { it.cont } && sum == fibSum
      }
    }

    "Schedule.linear()" {
      forFew(5, Gen.intSmall().filter { it > 0 }.filter { it < 10 }, Gen.intSmall().filter { it > 0 }.filter { it < 10 }) { i, n ->
        val res = Schedule.linear<ForId, Any?>(Id.monad(), i.seconds).runIdSchedule(0, n)

        val sum = res.fold(0L) { acc, v -> acc + v.delay.amount }
        val expSum = linear(i.toLong()).drop(1).take(n).sum()

        res.forAll { it.cont } && sum == expSum
      }
    }

    "Schedule.exponential()" {
      forFew(5, Gen.intSmall().filter { it > 0 }.filter { it < 10 }, Gen.intSmall().filter { it > 0 }.filter { it < 10 }) { i, n ->
        val res = Schedule.exponential<ForId, Any?>(Id.monad(), i.seconds).runIdSchedule(0, n)

        val sum = res.fold(0L) { acc, v -> acc + v.delay.amount }
        val expSum = exp(i.toLong()).drop(1).take(n).sum()

        res.forAll { it.cont } && sum == expSum
      }
    }

    "repeat" {
      forAll(Schedule.Decision.genK().genK(Gen.int()), Gen.intSmall().filter { it < 100 }.filter { it >= 0 }) { dec, n ->
        val schedule = Schedule(IO.monad(), IO.just(0 as Any?)) { _: Unit, _ -> IO.just(dec.fix()) }

        val eff = SideEffect()
        val ref = Ref(IO.monadDefer(), 0.seconds).fix().unsafeRunSync()

        val res = IO { if (eff.counter >= n) throw RuntimeException("WOOO") else eff.increment() }
          .repeat(IO.monadThrow(), refTimer(ref), schedule)
          .attempt()
          .fix().unsafeRunSync()

        if (dec.fix().cont || n == 0) res.isLeft() &&
          ref.get().fix().unsafeRunSync().nanoseconds == max(n - 1, 0) * dec.fix().delay.nanoseconds &&
          eff.counter == n
        else res.isRight() && eff.counter == 1 &&
          ref.get().fix().unsafeRunSync().nanoseconds == 0L &&
          (res as Either.Right).b == dec.fix().finish.value()
      }
    }

    "retry" {
      forAll(Schedule.Decision.genK().genK(Gen.int()), Gen.intSmall().filter { it < 100 }.filter { it >= 0 }) { dec, n ->
        val schedule = Schedule(IO.monad(), IO.just(0 as Any?)) { _: Throwable, _ -> IO.just(dec.fix()) }

        val eff = SideEffect()
        val ref = Ref(IO.monadDefer(), 0.seconds).fix().unsafeRunSync()

        val res = IO { if (eff.counter <= n) { eff.increment(); throw RuntimeException("WOOO") } else Unit }
          .retry(IO.monadThrow(), refTimer(ref), schedule)
          .attempt()
          .fix().unsafeRunSync()

        if (dec.fix().cont) res.isRight() &&
          eff.counter == n + 1 &&
          ref.get().fix().unsafeRunSync().nanoseconds == (n + 1) * dec.fix().delay.nanoseconds
        else res.isLeft() &&
          eff.counter == 1 &&
          ref.get().fix().unsafeRunSync().nanoseconds == 0L
      }
    }
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
