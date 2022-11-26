package arrow.fx.coroutines

import arrow.core.right
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class ParTraverseEitherJvmTest : ArrowFxSpec(
  spec = {
    "parTraverseEither finishes on single thread " { // 100 is same default length as Arb.list
      checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
        val res = resourceScope {
          val ctx = singleThreadContext("single")
          (0 until i).parTraverseEither(ctx) { Thread.currentThread().name.right() }
        }
        assertSoftly {
          res.orNull()?.forEach {
            it shouldStartWith "single"
          } ?: fail("Expected Right but found $res")
        }
      }
    }
  }
)
