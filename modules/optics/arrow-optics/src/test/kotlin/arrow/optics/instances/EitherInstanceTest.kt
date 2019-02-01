package arrow.optics.instances

import arrow.core.Either
import arrow.core.Option
import arrow.data.ListK
import arrow.data.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.either.each.each
import arrow.test.UnitSpec
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class EitherInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Either.each<String, Int>().each(),
      aGen = Gen.either(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

  }
}
