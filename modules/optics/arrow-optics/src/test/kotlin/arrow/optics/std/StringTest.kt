package arrow.optics

import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StringTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = String.toList(),
      aGen = Gen.string(),
      bGen = Gen.create { Gen.string().generate().toList() },
      funcGen = genFunctionAToB(Gen.create { Gen.string().generate().toList() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = object : Monoid<List<Char>> {
        override fun List<Char>.combine(b: List<Char>): List<Char> = this + b
        override fun empty(): List<Char> = emptyList()
      }
    ))

  }

}