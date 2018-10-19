package arrow.optics.instances

import arrow.core.*
import arrow.data.ListK
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.optics.instances.either.each.each
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Either.each<String, Int>().each(),
      aGen = genEither(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

  }
}
