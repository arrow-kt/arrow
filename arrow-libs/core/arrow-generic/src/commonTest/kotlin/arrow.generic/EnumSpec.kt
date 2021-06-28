package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable

@Serializable
enum class Example {
  A, B, C;
}

class EnumSpec : StringSpec({

  "Example enum" {
    Generic.encode(Example.A) shouldBe Generic.enum(Example.A)
  }
})
