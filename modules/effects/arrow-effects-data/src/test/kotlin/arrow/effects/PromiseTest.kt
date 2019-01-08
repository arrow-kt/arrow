package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.monad.F
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.generators.genThrowable
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PromiseTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<ForIO, A>> = Eq { a, b ->
    Option.eq(Either.eq(Eq.any(), Eq.any())).run {
      a.fix().attempt().unsafeRunTimed(60.seconds).eqv(b.fix().attempt().unsafeRunTimed(60.seconds))
    }
  }

  private fun <A> promise(): IO<Promise<ForIO, A>> = Promise.uncancelable<ForIO, A>(IO.async()).fix()

  init {

    "tryGet before completing" {
      promise<Int>().flatMap { p ->
        p.tryGet
      }.equalUnderTheLaw(IO.just(None), EQ())
    }

    "tryGet after completing" {
      forAll(Gen.int()) { i ->
        promise<Int>().flatMap { p ->
          p.complete(i).flatMap {
            p.tryGet
          }
        }.equalUnderTheLaw(IO.just(Some(i)), EQ())
      }
    }

    "complete" {
      forAll(Gen.int()) { i ->
        promise<Int>().flatMap { p ->
          p.complete(i).flatMap {
            p.get
          }
        }.equalUnderTheLaw(IO.just(i), EQ())
      }
    }

    "complete twice results in AlreadyFulfilled" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        F {
          val (p) = promise<Int>()
          p.complete(a).bind()
          p.complete(b).bind()
          p.get.bind()
        }.equalUnderTheLaw(IO.raiseError(Promise.AlreadyFulfilled), EQ())
      }
    }

    "tryComplete" {
      forAll(Gen.int()) { i ->
        F {
          val (p) = promise<Int>()
          p.tryComplete(i).bind() toT p.get.bind()
        }.equalUnderTheLaw(IO.just(true toT i), EQ())
      }
    }

    "tryComplete returns false if already complete" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        F {
          val (p) = promise<Int>()
          p.complete(a).bind()
          p.tryComplete(b).bind() toT p.get.bind()
        }.equalUnderTheLaw(IO.just(false toT a), EQ())
      }
    }

    "error" {
      val error = RuntimeException("Boom")
      promise<Int>().flatMap { p ->
        p.error(error).flatMap {
          p.get
        }
      }.equalUnderTheLaw(IO.raiseError(error), EQ())
    }

    "error after completion results in AlreadyFulfilled" {
      forAll(Gen.int(), genThrowable()) { i, t ->
        F {
          val (p) = promise<Int>()
          p.complete(i).bind()
          p.error(t).bind()
          p.get.bind()
        }.equalUnderTheLaw(IO.raiseError(Promise.AlreadyFulfilled), EQ())
      }
    }

    "tryError returns false if already completed" {
      forAll(Gen.int(), genThrowable()) { i, t ->
        F {
          val (p) = promise<Int>()
          p.complete(i).bind()
          p.tryError(t).bind() toT p.get.bind()
        }.equalUnderTheLaw(IO.just(false toT i), EQ())
      }
    }

    "tryError" {
      forAll(genThrowable()) { t ->
        F {
          val (p) = promise<Int>()
          p.tryError(t).bind()
        }.equalUnderTheLaw(IO.raiseError(t), EQ())
      }
    }

  }

}

