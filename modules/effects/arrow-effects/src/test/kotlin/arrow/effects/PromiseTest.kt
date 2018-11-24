package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.instances.io.applicative.applicative
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.async.continueOn
import arrow.effects.instances.io.monad.binding
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.instances.io.monad.map
import arrow.effects.instances.io.monad.monad
import arrow.effects.instances.io.monadDefer.monadDefer
import arrow.effects.typeclasses.seconds
import arrow.instances.either.eq.eq
import arrow.instances.eq
import arrow.instances.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genThrowable
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.Dispatchers
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
      forAll(Gen.int()) { i ->
        promise<Int>().flatMap { p ->
          p.tryGet
        }.equalUnderTheLaw(IO.just(None), EQ())
      }
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
        binding {
          val p = promise<Int>().bind()
          p.complete(a).bind()
          p.complete(b).bind()
          p.get.bind()
        }.equalUnderTheLaw(IO.raiseError(Promise.AlreadyFulfilled), EQ())
      }
    }

    "tryComplete" {
      forAll(Gen.int()) { i ->
        binding {
          val p = promise<Int>().bind()
          p.tryComplete(i).bind() toT p.get.bind()
        }.equalUnderTheLaw(IO.just(true toT i), EQ())
      }
    }

    "tryComplete returns false if already complete" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        binding {
          val p = promise<Int>().bind()
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
        binding {
          val p = promise<Int>().bind()
          p.complete(i).bind()
          p.error(t).bind()
          p.get.bind()
        }.equalUnderTheLaw(IO.raiseError(Promise.AlreadyFulfilled), EQ())
      }
    }

    "tryError returns false if already completed" {
      forAll(Gen.int(), genThrowable()) { i, t ->
        binding {
          val p = promise<Int>().bind()
          p.complete(i).bind()
          p.tryError(t).bind() toT p.get.bind()
        }.equalUnderTheLaw(IO.just(false toT i), EQ())
      }
    }

    "tryError" {
      forAll(genThrowable()) { t ->
        binding {
          val p = promise<Int>().bind()
          p.tryError(t).bind()
        }.equalUnderTheLaw(IO.raiseError(t), EQ())
      }
    }

  }

}

