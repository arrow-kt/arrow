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
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class PrimitiveSpec : StringSpec({
  testPrimitive(Arb.bool()) { Generic.Boolean(it) }
  testPrimitive(Arb.string()) { Generic.String(it) }
  testPrimitive(Arb.char()) { Generic.Char(it) }
  testPrimitive(Arb.byte()) { Generic.Number.Byte(it) }
  testPrimitive(Arb.short()) { Generic.Number.Short(it) }
  testPrimitive(Arb.int()) { Generic.Number.Int(it) }
  testPrimitive(Arb.long()) { Generic.Number.Long(it) }
  testPrimitive(Arb.float()) { Generic.Number.Float(it) }
  testPrimitive(Arb.double()) { Generic.Number.Double(it) }
})

inline fun <reified A> StringSpec.testPrimitive(
  arb: Arb<A>,
  noinline expected: (A) -> Generic<A>
): Unit =
  "${A::class.qualifiedName!!}" {
    checkAll(arb.orNull()) { a ->
      Generic.encode(a) shouldBe if (a == null) Generic.Null else expected(a)
    }
  }
