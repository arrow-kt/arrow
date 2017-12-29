package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Eq
import arrow.IsoLaws
import arrow.Monoid
import arrow.UnitSpec
import arrow.Validated
import arrow.genEither
import arrow.genFunctionAToB
import arrow.genValidated
import arrow.data.Invalid
import arrow.invalid
import arrow.valid
import arrow.data.Valid
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
                            when(a) {
                                is Invalid -> {
                                    when(b) {
                                        is Invalid -> (a.e + b.e).invalid()
                                        is Valid -> b
                                    }
                                }
                                is Valid -> {
                                    when(b) {
                                        is Invalid -> b
                                        is Valid -> (a.a + b.a).valid()
                                    }
                                }
                            }

                }
        ))
    }

}