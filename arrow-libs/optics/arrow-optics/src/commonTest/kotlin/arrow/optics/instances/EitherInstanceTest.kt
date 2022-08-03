package arrow.optics.instances

import arrow.optics.Traversal
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.either
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.TraversalLaws

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
