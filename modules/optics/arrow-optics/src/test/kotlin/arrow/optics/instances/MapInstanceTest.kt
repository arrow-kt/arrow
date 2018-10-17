package arrow.optics.instances

import arrow.core.*
import arrow.data.*
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.instances.option.monoid.monoid
import arrow.optics.instances.mapk.at.at
import arrow.optics.instances.mapk.each.each
import arrow.optics.instances.mapk.filterIndex.filterIndex
import arrow.optics.instances.mapk.index.index
import arrow.optics.typeclasses.FilterIndex
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.LensLaws
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = MapK.each<Int, String>().each(),
      aGen = genMapK(Gen.int(), Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = MapInstances.each<Int, String>().each(),
      aGen = Gen.map(Gen.int(), Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
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
      traversal = MapInstances.filterIndex<Char, Int>().filter { true },
      aGen = genMapK(genChars(), genIntSmall()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(OptionalLaws.laws(
      optional = MapK.index<String, Int>().index(Gen.string().generate()),
      aGen = genMapK(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = MapInstances.index<String, Int>().index(Gen.string().generate()),
      aGen = Gen.map(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(LensLaws.laws(
      lens = MapK.at<String, Int>().at(Gen.string().generate()),
      aGen = genMapK(Gen.string(), Gen.int()),
      bGen = genOption(Gen.int()),
      funcGen = genFunctionAToB(genOption(Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Option.monoid(Int.monoid())
    ))

    testLaws(LensLaws.laws(
      lens = MapInstances.at<String, Int>().at(Gen.string().generate()),
      aGen = Gen.map(Gen.string(), Gen.int()),
      bGen = genOption(Gen.int()),
      funcGen = genFunctionAToB(genOption(Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Option.monoid(Int.semigroup())
    ))

  }
}
