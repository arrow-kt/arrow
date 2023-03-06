package arrow.core.test

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

class GeneratorTest : StringSpec({
  "Arb.ior should generate left, right & both" {
    Arb.list(Arb.ior(Arb.string(), Arb.int())).next()
      .forAtLeastOne {
        it.isRight.shouldBeTrue()
      }
      .forAtLeastOne {
        it.isBoth.shouldBeTrue()
      }
      .forAtLeastOne {
        it.isLeft.shouldBeTrue()
      }
  }
})
