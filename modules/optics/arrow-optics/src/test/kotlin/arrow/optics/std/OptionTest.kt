package arrow.optics

import arrow.core.*
import arrow.instances.IntMonoidInstance
import arrow.instances.monoid
import arrow.instances.either.applicative.applicative
import arrow.instances.option.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genEither
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNullable
import arrow.test.generators.genOption
import arrow.test.laws.IsoLaws
import arrow.test.laws.PrismLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest : UnitSpec() {

  init {

    testLaws(PrismLaws.laws(
      prism = Option.some(),
      aGen = genOption(Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = Option.none(),
      aGen = genOption(Gen.int()),
      bGen = Gen.create { Unit },
      funcGen = genFunctionAToB(Gen.create { Unit }),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(IsoLaws.laws(
      iso = Option.toNullable<Int>().reverse(),
      aGen = genNullable(Gen.int()),
      bGen = genOption(Gen.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      funcGen = genFunctionAToB(genOption(Gen.int())),
      bMonoid = Option.monoid(Int.monoid())
    ))

    testLaws(IsoLaws.laws(
      iso = Option.toEither(),
      aGen = genOption(Gen.int()),
      bGen = genEither(Gen.create { Unit }, Gen.int()),
      funcGen = genFunctionAToB(genEither(Gen.create { Unit }, Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = object : Monoid<Either<Unit, Int>> {
        override fun Either<Unit, Int>.combine(b: Either<Unit, Int>): Either<Unit, Int> =
          Either.applicative<Unit>().run { this@combine.map2(b) { (a, b) -> a + b }.fix() }

        override fun empty(): Either<Unit, Int> = Right(0)
      }
    ))

  }
}
