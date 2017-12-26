package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Either
import arrow.Eq
import arrow.IsoLaws
import arrow.Monoid
import arrow.PrismLaws
import arrow.UnitSpec
import arrow.Validated
import arrow.applicative
import arrow.ev
import arrow.genEither
import arrow.genFunctionAToB
import arrow.genThrowable
import arrow.genTry
import arrow.genValidated
import arrow.invalid
import arrow.Invalid
import arrow.valid
import arrow.Valid
import arrow.syntax.either.right
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
                                is Invalid -> {
                                    when (b) {
                                        is Invalid -> (a.e).invalid()
                                        is Valid -> b
                                    }
                                }
                                is Valid -> {
                                    when (b) {
                                        is Invalid -> b
                                        is Valid -> (a.a + b.a).valid()
                                    }
                                }
                            }

                    override fun empty() = 0.valid<Throwable, Int>()
                })
        )

    }
}
