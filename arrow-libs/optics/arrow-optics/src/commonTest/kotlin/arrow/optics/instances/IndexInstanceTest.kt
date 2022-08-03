package arrow.optics.instances

import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.typeclasses.Index
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.laws.testLaws

class IndexInstanceTest : StringSpec() {

  init {
    testLaws(
      "Index list - ",
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.list<String>().index(it) },
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Index sequence - ",
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.sequence<String>().index(it) },
        aGen = Arb.sequence(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        eqa = { a, b -> a.toList() == b.toList() }
      )
    )

    testLaws(
      "Index map - ",
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.list<String>().index(it) },
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      )
    )

    testLaws(
      "Index Nel - ",
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.nonEmptyList<String>().index(it) },
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Index string - ",
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.string().index(it) },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )
  }
}
