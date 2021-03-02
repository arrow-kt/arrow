package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.list
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.typeclasses.Index
import io.kotlintest.properties.Gen

class IndexInstanceTest : UnitSpec() {

  init {
    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.list<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      )
    )
  }
}
