package arrow.optics.instances

import arrow.core.*
import arrow.core.extensions.eq
import arrow.data.*
import arrow.data.extensions.listk.eq.eq
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
import arrow.test.generators.*
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class ListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = ListK.each<String>().each(),
        aGen = genListK(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
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
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = ListK.filterIndex<String>().filter { true },
        aGen = genListK(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
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
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQListB = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { ListK.index<String>().index(it) },
        aGen = genListK(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { ListExtensions.index<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListK.cons<Int>().cons(),
        aGen = genListK(Gen.int()),
        bGen = genTuple(Gen.int(), genListK(Gen.int())),
        funcGen = genFunctionAToB(genTuple(Gen.int(), genListK(Gen.int()))),
        EQA = ListK.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(Int.eq(), ListK.eq(Int.eq())))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = ListK.snoc<Int>().snoc(),
        aGen = genListK(Gen.int()),
        bGen = genTuple(genListK(Gen.int()), Gen.int()),
        funcGen = genFunctionAToB(genTuple(genListK(Gen.int()), Gen.int())),
        EQA = ListK.eq(Int.eq()),
        EQOptionB = Option.eq(Tuple2.eq(ListK.eq(Int.eq()), Int.eq()))
      )
    )

  }
}
