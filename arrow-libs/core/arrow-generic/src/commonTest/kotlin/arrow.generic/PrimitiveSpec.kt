package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class PrimitiveSpec : StringSpec({

  "String" {
    checkAll(Arb.string()) { a ->
      Generic.encode(a) shouldBe Generic.String(a)
    }
  }

  "Char" {
    checkAll(Arb.char()) { a ->
      Generic.encode(a) shouldBe Generic.Char(a)
    }
  }

  "Byte" {
    checkAll(Arb.byte()) { a ->
      Generic.encode(a) shouldBe Generic.Number.Byte(a)
    }
  }

  "Short" {
    checkAll(Arb.short()) { a ->
      Generic.encode(a) shouldBe Generic.Number.Short(a)
    }
  }

  "Int" {
    checkAll(Arb.int()) { a ->
      Generic.encode(a) shouldBe Generic.Number.Int(a)
    }
  }

  "Long" {
    checkAll(Arb.long()) { a ->
      Generic.encode(a) shouldBe Generic.Number.Long(a)
    }
  }

  "Float" {
    checkAll(Arb.float()) { a ->
      Generic.encode(a) shouldBe Generic.Number.Float(a)
    }
  }

  "Double" {
    checkAll(Arb.double()) { a ->
      Generic.encode(a) shouldBe Generic.Number.Double(a)
    }
  }

  "Boolean" {
    checkAll(Arb.bool()) { a ->
      Generic.encode(a) shouldBe Generic.Boolean(a)
    }
  }
})
