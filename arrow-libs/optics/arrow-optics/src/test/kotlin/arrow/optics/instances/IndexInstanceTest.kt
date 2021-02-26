package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.list
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class IndexInstanceTest : UnitSpec() {

  init {

    listOf(1, 2, 3)
      .map { it + 1}

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.list<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any()
      )
    )
  }
}
