package arrow.optics.std

import arrow.core.Either
import arrow.core.Right
import arrow.core.extensions.either.applicative.applicative
import arrow.core.fix
import arrow.core.Validated
import arrow.optics.toEither
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.validated
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class ValidatedTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = Validated.toEither(),
        aGen = Gen.validated(Gen.string(), Gen.int()),
        bGen = Gen.either(Gen.string(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.string(), Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = object : Monoid<Either<String, Int>> {
          override fun empty() = Right(0)

          override fun Either<String, Int>.combine(b: Either<String, Int>): Either<String, Int> =
            Either.applicative<String>().run { this@combine.map2(b) { (a, b) -> a + b }.fix() }
        })
    )
  }
}
