package arrow.fx.coroutines

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ParTraverseJvmTest : StringSpec({
  "parTraverse runs on provided context " { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        val ctx = singleThreadContext("single")
        (0 until i).parTraverse(ctx) { Thread.currentThread().name }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
  
  "parTraverseN runs on provided thread" {
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        val ctx = singleThreadContext("single")
        (0 until i).parTraverseN(ctx, 3) {
          Thread.currentThread().name
        }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
})
