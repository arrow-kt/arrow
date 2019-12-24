package arrow.fx

import arrow.core.extensions.monoid
import arrow.fx.extensions.applicative
import arrow.fx.extensions.io.applicative.unit
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.monoid
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.FiberOf
import arrow.fx.typeclasses.FiberPartialOf
import arrow.fx.typeclasses.fix
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class FiberTest : UnitSpec() {

  init {
    fun FIBER_EQ(): Eq<FiberOf<IOPartialOf<Nothing>, Int>> = object : Eq<FiberOf<IOPartialOf<Nothing>, Int>> {
      override fun FiberOf<IOPartialOf<Nothing>, Int>.eqv(b: FiberOf<IOPartialOf<Nothing>, Int>): Boolean = EQ<Nothing, Int>().run {
        fix().join().eqv(b.fix().join())
      }
    }

    testLaws(
      ApplicativeLaws.laws<FiberPartialOf<IOPartialOf<Nothing>>>(Fiber.applicative(IO.concurrent()), FIBER_EQ()),
      MonoidLaws.laws(Fiber.monoid(IO.concurrent<Nothing>(), Int.monoid()), Gen.int().map { i ->
        Fiber(IO.just(i), IO.unit)
      }, FIBER_EQ())
    )
  }
}
