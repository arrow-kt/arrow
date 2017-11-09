package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IsoLaws
import kategory.Monoid
import kategory.UnitSpec
import kategory.Validated
import kategory.genEither
import kategory.genFunctionAToB
import kategory.genValidated
import kategory.Invalid
import kategory.invalid
import kategory.valid
import kategory.Valid
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