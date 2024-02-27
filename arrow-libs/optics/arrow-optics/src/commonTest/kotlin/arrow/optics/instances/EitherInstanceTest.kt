package arrow.optics.instances

import arrow.optics.Traversal
import arrow.optics.test.either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class EitherInstanceTest {
  @Test
  fun eitherLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.either(),
        aGen = Arb.either(Arb.string(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )
}
