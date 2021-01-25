package arrow.fx

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.None
import arrow.core.test.concurrency.SideEffect
import arrow.core.test.generators.GenK
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.applicative
import arrow.core.test.generators.intSmall
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.monad
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.extensions.io.monadThrow.monadThrow
import arrow.fx.extensions.schedule.applicative.applicative
import arrow.fx.test.eq.eqK
import arrow.fx.test.generators.genK
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import kotlin.math.max

class ScheduleTest : ArrowFxSpec() {

  fun <F, I> eqK(fEqK: EqK<F>, MF: Monad<F>, i: I): EqK<SchedulePartialOf<F, I>> = object : EqK<SchedulePartialOf<F, I>> {
    override fun <A> Kind<SchedulePartialOf<F, I>, A>.eqK(other: Kind<SchedulePartialOf<F, I>, A>, EQ: Eq<A>): Boolean {
      val t = fix() as Schedule.ScheduleImpl<F, Any?, I, A>
      (other as Schedule.ScheduleImpl<F, Any?, I, A>)

      return MF.run {
        val lhs = t.initialState.flatMap { s -> t.update(i, s) }
        val rhs = other.initialState.flatMap { s -> other.update(i, s) }

        fEqK.liftEq(Schedule.Decision.eqK().liftEq(EQ)).run { lhs.eqv(rhs) }
      }
    }
  }

  fun <F, I> eqK2(fEqK: EqK<F>, MF: Monad<F>, i: I) = object : EqK2<Kind<ForSchedule, F>> {
    override fun <A, B> Kind2<Kind<ForSchedule, F>, A, B>.eqK(other: Kind2<Kind<ForSchedule, F>, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
      ((this as Kind<SchedulePartialOf<F, I>, A>) to (other as Kind<SchedulePartialOf<F, I>, A>)).let {
        eqK(fEqK, MF, i).liftEq(EQA).run {
          it.first.eqv(it.second)
        }
      }
  }

  // This is a bad gen. But generating random schedules is weird
  fun <F, I> Schedule.Companion.genK(MF: Monad<F>): GenK<SchedulePartialOf<F, I>> = object : GenK<SchedulePartialOf<F, I>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<SchedulePartialOf<F, I>, A>> =
      gen.applicative(Schedule.applicative<F, I>(MF))
  }

  fun <F> Schedule.Companion.genK2(MF: Monad<F>) = object : GenK2<Kind<ForSchedule, F>> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<Kind<ForSchedule, F>, A, B>> =
      Schedule.genK<F, A>(MF).genK(genB)
  }

  fun refTimer(ref: Ref<ForIO, Duration>): Timer<ForIO> = object : Timer<ForIO> {
    override fun sleep(duration: Duration): Kind<ForIO, Unit> =
      ref.update { d -> d + duration }
  }

  init {
    "Schedule.never() should execute once and timeout" {
      val sideEffect = SideEffect()
      IO { sideEffect.increment() }.repeat(Schedule.never(IO.async()))
        .unsafeRunTimed(50.milliseconds) shouldBe None

      sideEffect.counter shouldBe 1
    }

    "repeat" {
      forAll(Schedule.Decision.genK().genK(Gen.int()), Gen.intSmall().filter { it in 0..99 }) { dec, n ->
        val schedule = Schedule(IO.monad(), IO.just(0 as Any?)) { _: Unit, _ -> IO.just(dec.fix()) }

        val eff = SideEffect()
        val ref = Ref(IO.monadDefer(), 0.seconds).fix().unsafeRunSync()

        val res = IO { if (eff.counter >= n) throw RuntimeException("WOOO") else eff.increment() }
          .repeat(IO.monadThrow(), refTimer(ref), schedule)
          .attempt()
          .fix().unsafeRunSync()

        if (dec.fix().cont || n == 0) {
          res.isLeft() shouldBe true
          ref.get().fix().unsafeRunSync().nanoseconds shouldBe max(n, 0) * dec.fix().delay.nanoseconds
          eff.counter shouldBe n
        } else {
          res.isRight() shouldBe true
          eff.counter shouldBe 1
          ref.get().fix().unsafeRunSync().nanoseconds shouldBe 0L
          (res as Either.Right).b shouldBe dec.fix().finish.value()
        }

        true
      }
    }

    "retry" {
      forAll(Schedule.Decision.genK().genK(Gen.int()), Gen.intSmall().filter { it < 100 }.filter { it >= 0 }) { dec, n ->
        val schedule = Schedule(IO.monad(), IO.just(0 as Any?)) { _: Throwable, _ -> IO.just(dec.fix()) }

        val eff = SideEffect()
        val ref = Ref(IO.monadDefer(), 0.seconds).fix().unsafeRunSync()

        val res = IO {
          if (eff.counter <= n) {
            eff.increment(); throw RuntimeException("WOOO")
          } else Unit
        }
          .retry(IO.monadThrow(), refTimer(ref), schedule)
          .attempt()
          .fix().unsafeRunSync()

        if (dec.fix().cont) {
          res.isRight() shouldBe true
          eff.counter shouldBe n + 1
          ref.get().fix().unsafeRunSync().nanoseconds shouldBe (n + 1) * dec.fix().delay.nanoseconds
        } else {
          res.isLeft() shouldBe true
          eff.counter shouldBe 1
          ref.get().fix().unsafeRunSync().nanoseconds shouldBe 0L
        }

        true
      }
    }
  }
}
