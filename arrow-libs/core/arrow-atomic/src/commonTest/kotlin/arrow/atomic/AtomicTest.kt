package arrow.atomic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class AtomicTest : StringSpec({

    "getAndSet - successful" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        val ref = Atomic(x)
        ref.getAndSet(y) shouldBe x
        ref.value shouldBe y
      }
    }

    "tryUpdate - modification occurs successfully" {
      checkAll(Arb.int()) { x ->
        val ref = Atomic(x)
        ref.tryUpdate { it + 1 }
        ref.value shouldBe x + 1
      }
    }

    "tryUpdate - should fail to update if modification has occurred" {
      checkAll(Arb.int()) { x ->
        val ref = Atomic(x)
        ref.tryUpdate {
          suspend { ref.update(Int::inc) }
            .startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { })
          it + 1
        } shouldBe false
      }
    }

    "concurrent modifications" {
      val finalValue = 50_000
      val r = Atomic(0)
      (0 until finalValue).forEach { r.update { it + 1 } }
      r.value shouldBe finalValue
    }
  }
)
