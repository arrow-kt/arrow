package arrow.optics.instances

import arrow.core.ListK
import arrow.core.MapK
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.extensions.eq
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.sequencek.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.listK
import arrow.core.test.generators.mapK
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.sequenceK
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.filterMapIndex
import arrow.optics.extensions.listk.filterIndex.filterIndex
import arrow.optics.extensions.mapk.filterIndex.filterIndex
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.extensions.sequencek.filterIndex.filterIndex
import arrow.optics.list
import arrow.optics.test.generators.char
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.FilterIndex
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class FilterIndexInstanceTest : UnitSpec() {

  init {
    testLaws(TraversalLaws.laws(
      traversal = ListK.filterIndex<String>().filter { true },
      aGen = Gen.listK(Gen.string()),
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.list<String>().filter { true },
      aGen = Gen.list(Gen.string()),
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = NonEmptyList.filterIndex<String>().filter { true },
      aGen = Gen.nonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = SequenceK.filterIndex<Char>().filter { true },
      aGen = Gen.sequenceK(Gen.char()),
      bGen = Gen.char(),
      funcGen = Gen.functionAToB(Gen.char()),
      EQA = SequenceK.eq(Char.eq()),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = MapK.filterIndex<Char, Int>().filter { true },
      aGen = Gen.mapK(Gen.char(), Gen.intSmall()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = filterMapIndex<Char, Int>().filter { true },
      aGen = Gen.map(Gen.char(), Gen.intSmall()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = String.filterIndex().filter { true },
      aGen = Gen.string(),
      bGen = Gen.char(),
      funcGen = Gen.functionAToB(Gen.char()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
