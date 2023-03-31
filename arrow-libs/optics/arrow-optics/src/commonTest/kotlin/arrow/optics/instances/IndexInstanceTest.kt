package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.nonEmptyList
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.sequence
import arrow.optics.typeclasses.Index
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

class IndexInstanceTest : StringSpec({

    testLaws(
      "Index list - ",
      OptionalLaws(
        optionalGen = Arb.int().map { Index.list<Long>().index(it) },
        aGen = Arb.list(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long()),
      )
    )

    testLaws(
      "Index sequence - ",
      OptionalLaws(
        optionalGen = Arb.int().map { Index.sequence<Long>().index(it) },
        aGen = Arb.sequence(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long()),
        eqa = { a, b -> a.toList() == b.toList() }
      )
    )

    testLaws(
      "Index map - ",
      OptionalLaws(
        optionalGen = Arb.int().map { Index.list<Long>().index(it) },
        aGen = Arb.list(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long())
      )
    )

    testLaws(
      "Index Nel - ",
      OptionalLaws(
        optionalGen = Arb.int().map { Index.nonEmptyList<Long>().index(it) },
        aGen = Arb.nonEmptyList(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long()),
      )
    )

    testLaws(
      "Index string - ",
      OptionalLaws(
        optionalGen = Arb.int().map { Index.string().index(it) },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )

})
