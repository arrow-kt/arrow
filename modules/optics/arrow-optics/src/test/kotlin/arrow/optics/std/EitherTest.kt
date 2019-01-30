package arrow.optics

import arrow.core.Either
import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class EitherTest : UnitSpec() {

  init {
    val VAL_MONOID: Monoid<Validated<String, Int>> = object : Monoid<Validated<String, Int>> {
      override fun empty() = Valid(0)

      override fun Validated<String, Int>.combine(b: Validated<String, Int>): Validated<String, Int> =
        when (this) {
          is Invalid -> {
            when (b) {
              is Invalid -> Invalid((e + b.e))
              is Valid -> b
            }
          }
          is Valid -> {
            when (b) {
              is Invalid -> b
              is Valid -> Valid((a + b.a))
            }
          }
        }

    }
    testLaws(IsoLaws.laws(
      iso = Either.toValidated(),
      aGen = Gen.either(Gen.string(), Gen.int()),
      bGen = Gen.validated(Gen.string(), Gen.int()),
      funcGen = Gen.functionAToB(Gen.validated(Gen.string(), Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = VAL_MONOID
    ))
  }

}