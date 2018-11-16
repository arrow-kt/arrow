package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.toT
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
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PromiseTest : UnitSpec() {

  val EQ: Eq<Kind<ForIO, Int>> = Eq { a, b ->
    Option.eq(Either.eq(Eq.any(), Int.eq())).run {
      a.fix().attempt().unsafeRunTimed(60.seconds).eqv(b.fix().attempt().unsafeRunTimed(60.seconds))
    }
  }

  private fun <A> promise(): IO<Promise<ForIO, A>> = Promise.uncancelable<ForIO, A>(IO.async()).fix()

  init {

    "complete" {
      forAll(Gen.int()) { i ->
        promise<Int>().flatMap { p ->
          p.complete(i).flatMap {
            p.get
          }
        }.equalUnderTheLaw(IO.just(i), EQ)
      }
    }

    "complete is only successful once" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        binding {
          val p = promise<Int>().bind()
          p.complete(a).bind()
          val succ = p.complete(b).bind()
          val aa = p.get.bind()
          a.equalUnderTheLaw(aa, Int.eq()) && !succ
        }.unsafeRunSync()
      }
    }

  }

}

