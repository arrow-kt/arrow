package arrow.optics.instances

import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.ListK
import arrow.instances.eq
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.instances.tuple2.eq.eq
import arrow.optics.instances.listk.snoc.snoc
import arrow.instances.tuple2.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genChars
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListK
import arrow.test.generators.genTuple
import arrow.test.generators.genTuple
import arrow.test.laws.OptionalLaws
import arrow.test.laws.PrismLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StringInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = String.each().each(),
      aGen = Gen.string(),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
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

    testLaws(OptionalLaws.laws(
      optional = String.index().index(5),
      aGen = Gen.string(),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = String.cons().cons(),
      aGen = Gen.string(),
      bGen = genTuple(genChars(), Gen.string()),
      funcGen = genFunctionAToB(genTuple(genChars(), Gen.string())),
      EQA = String.eq(),
      EQOptionB = Option.eq(Tuple2.eq(Char.eq(), String.eq()))
    ))

    testLaws(PrismLaws.laws(
      prism = String.snoc().snoc(),
      aGen = Gen.string(),
      bGen = genTuple(Gen.string(), genChars()),
      funcGen = genFunctionAToB(genTuple(Gen.string(), genChars())),
      EQA = String.eq(),
      EQOptionB = Option.eq(Tuple2.eq(String.eq(), Char.eq()))
    ))

  }
}
