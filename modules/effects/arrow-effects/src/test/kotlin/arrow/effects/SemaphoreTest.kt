package arrow.effects

import arrow.Kind
import arrow.effects.instances.io.applicative.applicative
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.typeclasses.seconds
import arrow.instances.either.eq.eq
import arrow.instances.eq
import arrow.instances.list.traverse.traverse
import arrow.instances.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SemaphoreTest : UnitSpec() {

  fun <A> EQ(EQA: Eq<A>): Eq<Kind<ForIO, A>> = Eq { a, b ->
    arrow.core.Option.eq(arrow.core.Either.eq(Eq.any(), EQA)).run {
      a.fix().attempt().unsafeRunTimed(60.seconds).eqv(b.fix().attempt().unsafeRunTimed(60.seconds))
    }
  }

  private fun semaphore(n: Long): IOOf<Semaphore<ForIO>> = Semaphore.uncancelable(n, IO.async())

  init {

    "acquire n synchronously" {
      val n = 20L
      semaphore(n).flatMap { s ->
        (0 until n).toList().traverse(IO.applicative()) { s.acquire }.flatMap {
          s.available
        }
      }.equalUnderTheLaw(IO.just(0L), EQ(Long.eq()))
    }

    "tryAcquire with available permits" {
      val n = 20
      semaphore(20).flatMap { s ->
        (0 until n).toList().traverse(IO.applicative()) { s.acquire }.flatMap {
          s.tryAcquire
        }
      }.equalUnderTheLaw(IO.just(true), EQ(Boolean.eq()))
    }

    "tryAcquire with no available permits" {
      val n = 20
      semaphore(20).flatMap { s ->
        (0 until n).toList().traverse(IO.applicative()) { s.acquire }.flatMap {
          s.tryAcquire
        }
      }.equalUnderTheLaw(IO.just(false), EQ(Boolean.eq()))
    }

  }

}