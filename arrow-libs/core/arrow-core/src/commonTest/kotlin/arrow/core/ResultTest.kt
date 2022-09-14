package arrow.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ResultTest : StringSpec({
  
  "null zip null" {
    val x = Result.success<Int?>(null)
    x.zip(x) { y, z ->
      (y?.plus(z ?: -2)) ?: -1
    } shouldBe Result.success(-1)
  }
  
})
