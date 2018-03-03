package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Monoid
import arrow.test.UnitSpec
import arrow.test.generators.genEither
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genValidated
import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import arrow.syntax.validated.invalid
import arrow.syntax.validated.valid
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherInstancesTest : UnitSpec() {

    init {
        testLaws(IsoLaws.laws(
                iso = eitherToValidated(),
                aGen = genEither(Gen.string(), Gen.int()),
                bGen = genValidated(Gen.string(), Gen.int()),
                funcGen = genFunctionAToB(genValidated(Gen.string(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = object : Monoid<Validated<String, Int>> {
                    override fun empty() = 0.valid<String, Int>()

                    override fun combine(a: Validated<String, Int>, b: Validated<String, Int>): Validated<String, Int> =
                            when (a) {
                                is Invalid -> {
                                    when (b) {
                                        is Invalid -> (a.e + b.e).invalid()
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

                }
        ))
    }

}