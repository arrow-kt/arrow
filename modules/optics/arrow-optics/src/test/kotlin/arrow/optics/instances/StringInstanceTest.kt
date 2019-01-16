package arrow.optics.instances

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.data.ListK
import arrow.data.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.optics.extensions.*
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class StringInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = String.each().each(),
        aGen = Gen.string(),
        bGen = genChar(),
        funcGen = genFunctionAToB(genChar()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = String.filterIndex().filter { true },
        aGen = Gen.string(),
        bGen = genChar(),
        funcGen = genFunctionAToB(genChar()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { String.index().index(it) },
        aGen = Gen.string(),
        bGen = genChar(),
        funcGen = genFunctionAToB(genChar()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = String.cons().cons(),
        aGen = Gen.string(),
        bGen = genTuple(genChar(), Gen.string()),
        funcGen = genFunctionAToB(genTuple(genChar(), Gen.string())),
        EQA = String.eq(),
        EQOptionB = Option.eq(Tuple2.eq(Char.eq(), String.eq()))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = String.snoc().snoc(),
        aGen = Gen.string(),
        bGen = genTuple(Gen.string(), genChar()),
        funcGen = genFunctionAToB(genTuple(Gen.string(), genChar())),
        EQA = String.eq(),
        EQOptionB = Option.eq(Tuple2.eq(String.eq(), Char.eq()))
      )
    )

  }
}
