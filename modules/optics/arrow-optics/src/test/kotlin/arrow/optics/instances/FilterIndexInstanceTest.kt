package arrow.optics.instances

import arrow.core.Option
import arrow.core.eq
import arrow.data.*
import arrow.instances.eq
import arrow.optics.typeclasses.FilterIndex
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FilterIndexInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.filterIndex(SequenceK.filterIndex()) { true },
      aGen = genSequenceK(genChars()),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQA = SequenceK.eq(Char.eq()),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

  }

}