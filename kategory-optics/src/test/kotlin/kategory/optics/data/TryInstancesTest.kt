package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Either
import kategory.Eq
import kategory.IsoLaws
import kategory.Monoid
import kategory.PrismLaws
import kategory.UnitSpec
import kategory.Validated
import kategory.applicative
import kategory.ev
import kategory.genEither
import kategory.genFunctionAToB
import kategory.genThrowable
import kategory.genTry
import kategory.genValidated
import kategory.invalid
import kategory.right
import kategory.valid
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TryInstancesTest : UnitSpec() {
    init {

        testLaws(
            PrismLaws.laws(
                prism = trySuccess(),
                aGen = genTry(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = tryFailure(),
                aGen = genTry(Gen.int()),
                bGen = genThrowable(),
                funcGen = genFunctionAToB(genThrowable()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            IsoLaws.laws(
                iso = tryToEither(),
                aGen = genTry(Gen.int()),
                bGen = genEither(genThrowable(), Gen.int()),
                funcGen = genFunctionAToB(genEither(genThrowable(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = object : Monoid<Either<Throwable, Int>> {
                    override fun combine(a: Either<Throwable, Int>, b: Either<Throwable, Int>): Either<Throwable, Int> =
                            Either.applicative<Throwable>().map2(a, b) { (a, b) -> a + b }.ev()

                    override fun empty(): Either<Throwable, Int> = 0.right()
                }),

            IsoLaws.laws(
                iso = tryToValidated(),
                aGen = genTry(Gen.int()),
                bGen = genValidated(genThrowable(), Gen.int()),
                funcGen = genFunctionAToB(genValidated(genThrowable(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = object : Monoid<Validated<Throwable, Int>> {
                    override fun combine(a: Validated<Throwable, Int>, b: Validated<Throwable, Int>): Validated<Throwable, Int> =
                            when (a) {
                                is Validated.Invalid -> {
                                    when (b) {
                                        is Validated.Invalid -> (a.e).invalid()
                                        is Validated.Valid -> b
                                    }
                                }
                                is Validated.Valid -> {
                                    when (b) {
                                        is Validated.Invalid -> b
                                        is Validated.Valid -> (a.a + b.a).valid()
                                    }
                                }
                            }

                    override fun empty() = 0.valid<Throwable, Int>()
                })
        )

    }
}
