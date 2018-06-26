package arrow.optics.instances

import arrow.core.*
import arrow.data.ListK
import arrow.instances.eq
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TryInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Try.each<String>().each(),
      aGen = genTry(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

  }
}
