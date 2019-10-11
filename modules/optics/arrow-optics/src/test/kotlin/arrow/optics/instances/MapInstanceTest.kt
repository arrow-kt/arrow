package arrow.optics.instances

import arrow.core.MapInstances
import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.monoid.monoid
import arrow.core.extensions.semigroup
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.extensions.listk.eq.eq
import arrow.optics.extensions.at
import arrow.optics.extensions.each
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.index
import arrow.optics.extensions.mapk.at.at
import arrow.optics.extensions.mapk.each.each
import arrow.optics.extensions.mapk.filterIndex.filterIndex
import arrow.optics.extensions.mapk.index.index
import arrow.test.UnitSpec
import arrow.test.generators.char
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.test.generators.mapK
import arrow.test.generators.option
import arrow.test.laws.LensLaws
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class MapInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = MapK.each<Int, String>().each(),
        aGen = Gen.mapK(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapInstances.each<Int, String>().each(),
        aGen = Gen.map(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapK.filterIndex<Char, Int>().filter { true },
        aGen = Gen.mapK(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = MapInstances.filterIndex<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.string().map { MapK.index<String, Int>().index(it) },
        aGen = Gen.mapK(Gen.string(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.string().map { MapInstances.index<String, Int>().index(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { MapK.at<String, Int>().at(it) },
        aGen = Gen.mapK(Gen.string(), Gen.int()),
        bGen = Gen.option(Gen.int()),
        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Option.monoid(Int.monoid())
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { MapInstances.at<String, Int>().at(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.option(Gen.int()),
        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Option.monoid(Int.semigroup())
      )
    )
  }
}
