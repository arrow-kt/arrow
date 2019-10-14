package arrow.optics.instances

import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.optics.extensions.each
import arrow.optics.extensions.filterIndex
import arrow.optics.extensions.index
import arrow.optics.extensions.listk.cons.cons
import arrow.optics.extensions.listk.each.each
import arrow.optics.extensions.listk.filterIndex.filterIndex
import arrow.optics.extensions.listk.index.index
import arrow.optics.extensions.listk.snoc.snoc
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.listK
import arrow.test.generators.tuple2
import arrow.test.laws.OptionalLaws
import arrow.test.laws.PrismLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class ListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = ListK.each<String>().each(),
        aGen = Gen.listK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListExtensions.each<String>().each(),
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListK.filterIndex<String>().filter { true },
        aGen = Gen.listK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListExtensions.filterIndex<String>().filter { true },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { ListK.index<String>().index(it) },
        aGen = Gen.listK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { ListExtensions.index<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListK.cons<Int>().cons(),
        aGen = Gen.listK(Gen.int()),
        bGen = Gen.tuple2(Gen.int(), Gen.listK(Gen.int())),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.listK(Gen.int()))),
        EQA = ListK.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(Int.eq(), ListK.eq(Int.eq())))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListK.snoc<Int>().snoc(),
        aGen = Gen.listK(Gen.int()),
        bGen = Gen.tuple2(Gen.listK(Gen.int()), Gen.int()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.listK(Gen.int()), Gen.int())),
        EQA = ListK.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(ListK.eq(Int.eq()), Int.eq()))
      )
    )
  }
}
