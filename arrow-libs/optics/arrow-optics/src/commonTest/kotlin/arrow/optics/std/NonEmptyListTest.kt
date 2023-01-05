package arrow.optics.std

import arrow.optics.Lens
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string

class NonEmptyListTest : StringSpec({

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

})
