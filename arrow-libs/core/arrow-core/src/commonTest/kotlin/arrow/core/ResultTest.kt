package arrow.core

import arrow.core.raise.result
import arrow.core.test.result
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ResultTest : StringSpec({

  "null zip null" {
    val x = Result.success<Int?>(null)
    x.zip(x) { y, z ->
      (y?.plus(z ?: -2)) ?: -1
    } shouldBe Result.success(-1)
  }

})
