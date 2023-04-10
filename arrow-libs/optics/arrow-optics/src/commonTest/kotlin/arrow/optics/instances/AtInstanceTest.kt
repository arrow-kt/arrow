package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.option
import arrow.optics.typeclasses.At
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set

class AtInstanceTest : StringSpec({
    testLaws(
      "At map - ",
      LensLaws(
        lensGen = Arb.long().map { At.map<Long, Int>().at(it) },
        aGen = Arb.map(Arb.long(), Arb.int()),
        bGen = Arb.option(Arb.int()),
        funcGen = Arb.functionAToB(Arb.option(Arb.int())),
      )
    )

    testLaws(
      "At set - ",
      LensLaws(
        lensGen = Arb.long().map { At.set<Long>().at(it) },
        aGen = Arb.set(Arb.long()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )
})
