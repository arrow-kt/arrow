package arrow.optics.std

import arrow.optics.Lens
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.core.nonEmptyList
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.LensLaws

class NonEmptyListTest : StringSpec() {

  init {

    testLaws(
      "Lens Nel head - ",
      LensLaws.laws(
        lens = Lens.nonEmptyListHead(),
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Lens Nel tail - ",
      LensLaws.laws(
        lens = Lens.nonEmptyListTail(),
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.list(Arb.string()),
        funcGen = Arb.functionAToB(Arb.list(Arb.string())),
      )
    )
  }
}
