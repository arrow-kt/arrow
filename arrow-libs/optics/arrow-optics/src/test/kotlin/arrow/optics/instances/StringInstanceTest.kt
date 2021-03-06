package arrow.optics.instances

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.tuple2
import arrow.optics.Traversal
import arrow.optics.string
import arrow.optics.test.generators.char
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class StringInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.string(),
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.string().filter { true },
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.string().index(it) },
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Cons.string().cons(),
        aGen = Gen.string(),
        bGen = Gen.tuple2(Gen.char(), Gen.string()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.char(), Gen.string())),
        EQA = String.eq(),
        EQOptionB = Option.eq(Tuple2.eq(Char.eq(), String.eq()))
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Snoc.string().snoc(),
        aGen = Gen.string(),
        bGen = Gen.tuple2(Gen.string(), Gen.char()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.string(), Gen.char())),
        EQA = String.eq(),
        EQOptionB = Option.eq(Tuple2.eq(String.eq(), Char.eq()))
      )
    )
  }
}
