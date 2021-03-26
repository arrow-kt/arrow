package arrow.optics.std

import arrow.core.Either
import arrow.core.Option
import arrow.core.Right
import arrow.core.extensions.monoid
import arrow.core.extensions.option.monoid.monoid
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.core.zip
import arrow.optics.Iso
import arrow.optics.Prism
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.PrismLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class OptionTest : UnitSpec() {

  init {

    testLaws(
      PrismLaws.laws(
        prism = Prism.some(),
        aGen = Gen.option(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Prism.none(),
        aGen = Gen.option(Gen.int()),
        bGen = Gen.create { Unit },
        funcGen = Gen.functionAToB(Gen.create { Unit }),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      IsoLaws.laws(
        iso = Iso.optionToNullable<Int>().reverse(),
        aGen = Gen.int().orNull(),
        bGen = Gen.option(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
        bMonoid = Option.monoid(Int.monoid())
      )
    )

    testLaws(
      IsoLaws.laws(
        iso = Iso.optionToEither(),
        aGen = Gen.option(Gen.int()),
        bGen = Gen.either(Gen.create { Unit }, Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.create { Unit }, Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = object : Monoid<Either<Unit, Int>> {
          override fun Either<Unit, Int>.combine(b: Either<Unit, Int>): Either<Unit, Int> =
            this.zip(b) { a, b -> a + b }

          override fun empty(): Either<Unit, Int> = Right(0)
        }
      )
    )
  }
}
