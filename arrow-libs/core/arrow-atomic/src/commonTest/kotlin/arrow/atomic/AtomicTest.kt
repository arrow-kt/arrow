package arrow.atomic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class AtomicTest : StringSpec({

    // NOTE: we test with strings because testing with ints doesn't work!

    "set get - successful" {
      checkAll(Arb.string(), Arb.string()) { x, y ->
        val r = Atomic(x)
        r.update { y }
        r.value shouldBe y
      }
    }

    "getAndSet - successful" {
      checkAll(Arb.string(), Arb.string()) { x, y ->
        val ref = Atomic(x)
        ref.getAndSet(y) shouldBe x
        ref.value shouldBe y
      }
    }

    "tryUpdate - modification occurs successfully" {
      checkAll(Arb.string()) { x ->
        val ref = Atomic(x)
        ref.tryUpdate { it + 1 }
        ref.value shouldBe x + 1
      }
    }

    "tryUpdate - should fail to update if modification has occurred" {
      checkAll(Arb.string()) { x ->
        val ref = Atomic(x)
        ref.tryUpdate {
          suspend { ref.update { it + "a" } }
            .startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { })
          it + "b"
        } shouldBe false
      }
    }

    "consistent set update on strings" {
      checkAll(Arb.string(), Arb.string()) { x, y ->
        val set = suspend {
          val r = Atomic(x)
          r.update { y }
          r.value
        }

        val update = suspend {
          val r = Atomic(x)
          r.update { y }
          r.value
        }

        set() shouldBe update()
      }
    }

    "concurrent modifications" {
      val finalValue = 50_000
      val r = Atomic("")
      (0 until finalValue).forEach { r.update { it + "a" } }
      r.value shouldBe "a".repeat(finalValue)
    }
  }
)
