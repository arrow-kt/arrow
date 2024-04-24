package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.typeclasses.Snoc
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class SnocInstanceTest {
  @Test
  fun snocListLaws() =
    testLaws(
      PrismLaws(
        prism = Snoc.list<Int>().snoc(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.pair(Arb.list(Arb.int()), Arb.int()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.list(Arb.int()), Arb.int())),
      )
    )

  @Test
  fun snocStringLaws() =
    testLaws(
      PrismLaws(
        prism = Snoc.string().snoc(),
        aGen = Arb.string(),
        bGen = Arb.pair(Arb.string(), Arb.char()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.string(), Arb.char())),
      )
    )
}
