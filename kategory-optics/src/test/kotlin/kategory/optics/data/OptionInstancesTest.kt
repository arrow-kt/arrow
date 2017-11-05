package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Either
import kategory.Eq
import kategory.IntMonoid
import kategory.IsoLaws
import kategory.Monoid
import kategory.OptionMonoidInstanceImplicits
import kategory.PrismLaws
import kategory.UnitSpec
import kategory.applicative
import kategory.binding
import kategory.ev
import kategory.flatMap
import kategory.genEither
import kategory.genFunctionAToB
import kategory.genNullable
import kategory.genOption
import kategory.monad
import kategory.pure
import kategory.right
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
