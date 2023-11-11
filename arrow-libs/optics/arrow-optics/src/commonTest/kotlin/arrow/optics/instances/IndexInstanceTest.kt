package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.nonEmptyList
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.typeclasses.Index
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class IndexInstanceTest {

  @Test
  fun indexListLaws() =
    testLaws(
      OptionalLaws(
        optionalGen = Arb.int().map { Index.list<Long>().index(it) },
        aGen = Arb.list(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long()),
      )
    )

  @Test
  fun indexSequenceLaws() =
    testLaws(
      OptionalLaws(
        optionalGen = Arb.int().map { Index.sequence<Long>().index(it) },
        aGen = Arb.list(Arb.long()).map { it.asSequence() },
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long()),
        eqa = { a, b -> a.toList() == b.toList() }
      )
    )

  @Test
  fun indexMapLaws() =
    testLaws(
      OptionalLaws(
        optionalGen = Arb.int().map { Index.list<Long>().index(it) },
        aGen = Arb.list(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long())
      )
    )

  @Test
  fun indexNonEmptyListLaws() =
    testLaws(
      OptionalLaws(
        optionalGen = Arb.int().map { Index.nonEmptyList<Long>().index(it) },
        aGen = Arb.nonEmptyList(Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.long()),
      )
    )

  @Test
  fun indexStringLaws() =
    testLaws(
      OptionalLaws(
        optionalGen = Arb.int().map { Index.string().index(it) },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )

}
