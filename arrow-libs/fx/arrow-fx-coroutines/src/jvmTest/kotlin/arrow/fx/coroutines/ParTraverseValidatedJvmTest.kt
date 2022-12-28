package arrow.fx.coroutines

import arrow.core.orNull
import arrow.core.validNel
import arrow.typeclasses.Semigroup
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ParTraverseValidatedJvmTest : StringSpec({
  "parTraverseValidated finishes on single thread " { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = single.use { ctx ->
        (0 until i).parTraverseValidated(ctx, Semigroup.nonEmptyList()) { Thread.currentThread().name.validNel() }
      }
      assertSoftly {
        res.orNull()?.forEach {
          it shouldStartWith "single"
        } ?: fail("Expected Right but found $res")
      }
    }
  }
})
