package arrow.optics.instances

import arrow.core.Option
import arrow.data.ListK
import arrow.data.MapK
import arrow.data.NonEmptyList
import arrow.data.SequenceK
import arrow.instances.eq
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.instances.sequencek.eq.eq
import arrow.optics.instances.listk.filterIndex.filterIndex
import arrow.optics.instances.mapk.filterIndex.filterIndex
import arrow.optics.instances.nonemptylist.filterIndex.filterIndex
import arrow.optics.instances.sequencek.filterIndex.filterIndex
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
      traversal = ListK.filterIndex<String>().filter { true },
      aGen = genListK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = ListFilterIndexInstance<String>().filter { true },
      aGen = Gen.list(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = NonEmptyList.filterIndex<String>().filter { true },
      aGen = genNonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = SequenceK.filterIndex<Char>().filter { true },
      aGen = genSequenceK(genChars()),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQA = SequenceK.eq(Char.eq()),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = MapK.filterIndex<Char, Int>().filter { true },
      aGen = genMapK(genChars(), genIntSmall()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = MapFilterIndexInstance<Char, Int>().filter { true },
      aGen = genMapK(genChars(), genIntSmall()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = String.filterIndex().filter { true },
      aGen = Gen.string(),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

  }

}