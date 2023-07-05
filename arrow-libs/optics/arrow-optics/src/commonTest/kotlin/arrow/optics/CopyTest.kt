package arrow.optics

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

class CopyTest : StringSpec({
  "copy functionality works with assignment" {
    checkAll(Arb.user()) { u ->
      val new = u.copy {
        User.token.ref = "Hello"
      }
      new.token.value shouldBe "Hello"
    }
  }
})
