package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class ListTest : UnitSpec() {

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
