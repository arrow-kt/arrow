package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Either
import arrow.Eq
import arrow.IntMonoid
import arrow.IsoLaws
import arrow.Monoid
import arrow.OptionMonoidInstanceImplicits
import arrow.PrismLaws
import arrow.UnitSpec
import arrow.applicative
import arrow.binding
import arrow.ev
import arrow.flatMap
import arrow.genEither
import arrow.genFunctionAToB
import arrow.genNullable
import arrow.genOption
import arrow.monad
import arrow.pure
import arrow.right
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionInstancesTest : UnitSpec() {

    init {

        testLaws(
            IsoLaws.laws(
                iso = nullableToOption<Int>(),
                aGen = genNullable(Gen.int()),
                bGen = genOption(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                funcGen = genFunctionAToB(genOption(Gen.int())),
                bMonoid = OptionMonoidInstanceImplicits.instance(IntMonoid)),

            PrismLaws.laws(
                prism = somePrism(),
                aGen = genOption(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = nonePrism(),
                aGen = genOption(Gen.int()),
                bGen = Gen.create { Unit },
                funcGen = genFunctionAToB(Gen.create { Unit }),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            IsoLaws.laws(
                iso = optionToEither(),
                aGen = genOption(Gen.int()),
                bGen = genEither(Gen.create { Unit }, Gen.int()),
                funcGen = genFunctionAToB(genEither(Gen.create { Unit }, Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = object : Monoid<Either<Unit, Int>> {
                    override fun combine(a: Either<Unit, Int>, b: Either<Unit, Int>): Either<Unit, Int> =
                            Either.applicative<Unit>().map2(a, b) { (a, b) -> a + b }.ev()

                    override fun empty(): Either<Unit, Int> = 0.right()
                })
        )

    }
}
