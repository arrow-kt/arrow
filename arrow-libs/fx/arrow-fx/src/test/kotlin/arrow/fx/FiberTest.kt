package arrow.fx

import arrow.core.extensions.monoid
import arrow.core.test.UnitSpec
import arrow.core.test.laws.ApplicativeLaws
import arrow.core.test.laws.MonoidLaws
import arrow.fx.extensions.applicative
import arrow.fx.extensions.functor
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.monoid
import arrow.fx.typeclasses.Fiber
import arrow.fx.test.eq.eq
import arrow.fx.test.eq.eqK
import arrow.fx.test.generators.genK
import io.kotlintest.properties.Gen

class FiberTest : UnitSpec() {

  init {
    testLaws(
      ApplicativeLaws.laws(Fiber.applicative(IO.concurrent()), Fiber.functor(IO.concurrent()), Fiber.genK(IO.applicative()), Fiber.eqK()),
      MonoidLaws.laws(Fiber.monoid(IO.concurrent(), Int.monoid()), Gen.int().map { i ->
        Fiber(IO.just(i), IO.unit)
      }, Fiber.eq(IO.eq()))
    )
  }
}
