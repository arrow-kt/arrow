package arrow.optics.instances

import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.test.laws.TraversalLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.laws.testLaws

class EitherInstanceTest : StringSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.either(),
        aGen = Arb.either(Arb.string(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )
  }
}
