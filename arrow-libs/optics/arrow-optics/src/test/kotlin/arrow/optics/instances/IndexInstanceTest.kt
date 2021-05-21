package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.typeclasses.Index
import io.kotest.property.Arb

class IndexInstanceTest : UnitSpec() {

  init {
    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.list<String>().index(it) },
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      )
    )
  }
}
