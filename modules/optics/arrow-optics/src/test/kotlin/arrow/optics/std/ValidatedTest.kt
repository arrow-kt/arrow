package arrow.optics

import arrow.core.Either
import arrow.core.Right
import arrow.core.Try
import arrow.core.extensions.`try`.applicative.applicative
import arrow.core.extensions.either.applicative.applicative
import arrow.core.fix
import arrow.core.Validated
import arrow.test.UnitSpec
import arrow.test.generators.`try`
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import arrow.test.generators.validated
import arrow.test.laws.IsoLaws
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
        }),

      IsoLaws.laws(
        iso = Validated.toTry(),
        aGen = Gen.validated(Gen.throwable(), Gen.int()),
        bGen = Gen.`try`(Gen.int()),
        funcGen = Gen.functionAToB(Gen.`try`(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = object : Monoid<Try<Int>> {
          override fun Try<Int>.combine(b: Try<Int>) = Try.applicative().run { this@combine.map2(b) { (a, b) -> a + b }.fix() }

          override fun empty(): Try<Int> = Try.Success(0)
        })
    )
  }
}
