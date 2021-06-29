package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.Serializable

@Serializable
inline class IString(val value: String)

class InlineSpec : StringSpec({

  "IString" {
    checkAll(Arb.string().map { IString(it) }) { istr ->
      Generic.encode(istr) shouldBe Generic.String(istr.value)
    }
  }

  "Nested generic IString" {
    checkAll(Arb.string().map { IString(it) }) { istr ->
      Generic.encode(Id(istr)) shouldBe Generic.String(istr.value).id()
    }
  }
})
