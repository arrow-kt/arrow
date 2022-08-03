package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.Optional
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.core.nonEmptyList
import io.kotest.property.arrow.core.option
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.IsoLaws
import io.kotest.property.arrow.optics.OptionalLaws
import io.kotest.property.arrow.optics.SetterLaws
import io.kotest.property.arrow.optics.TraversalLaws

class ListTest : StringSpec() {

  init {

    testLaws(
      "Optional list head - ",
      OptionalLaws.laws(
        optional = Optional.listHead(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),
      TraversalLaws.laws(
        traversal = Optional.listHead<Int>(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),
      SetterLaws.laws(
        setter = Optional.listHead<Int>(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Optional list tail - ",
      OptionalLaws.laws(
        optional = Optional.listTail(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.list(Arb.int()),
        funcGen = Arb.functionAToB(Arb.list(Arb.int())),
      )
    )

    testLaws(
      "Iso list to Option Nel - ",
      IsoLaws.laws(
        iso = Iso.listToOptionNel(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.option(Arb.nonEmptyList(Arb.int())),
        funcGen = Arb.functionAToB(Arb.option(Arb.nonEmptyList(Arb.int()))),
      )
    )
  }
}
