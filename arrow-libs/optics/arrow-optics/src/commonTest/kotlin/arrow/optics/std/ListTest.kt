package arrow.optics.std

import arrow.optics.Optional
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list

class ListTest : StringSpec({

    testLaws(
      "Optional list head - ",
      OptionalLaws(
        optional = Optional.listHead(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),
      TraversalLaws(
        traversal = Optional.listHead(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),
      SetterLaws(
        setter = Optional.listHead(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Optional list tail - ",
      OptionalLaws(
        optional = Optional.listTail(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.list(Arb.int()),
        funcGen = Arb.functionAToB(Arb.list(Arb.int())),
      )
    )

    testLaws(
      "Iso list to Option Nel - ",
      IsoLaws(
        iso = Iso.listToOptionNel(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.option(Arb.nonEmptyList(Arb.int())),
        funcGen = Arb.functionAToB(Arb.option(Arb.nonEmptyList(Arb.int()))),
      )
    )

})
