package arrow.fx.coroutines

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class AtomicTest : ArrowFxSpec(
  spec = {

    "set get - successful" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        val r = Atomic(x)
        r.set(y)
        r.get() shouldBe y
      }
    }

    "getAndSet - successful" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        val ref = Atomic(x)
        ref.getAndSet(y) shouldBe x
        ref.get() shouldBe y
      }
    }

    "access - successful" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        val ref = Atomic(x)
        val (_, setter) = ref.access()
        setter(y) shouldBe true
        ref.get() shouldBe y
      }
    }

    "access - setter should fail if value is modified before setter is called" {
      checkAll(Arb.int(), Arb.int()) { x, z ->
        val ref = Atomic(x)
        val (_, setter) = ref.access()
        ref.update { it + 1 }
        setter(z) shouldBe false
        ref.get() shouldBe x + 1
      }
    }

    "access - setter should fail if called twice" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int()) { w, x, y, z ->
        val ref = Atomic(w)
        val (_, setter) = ref.access()
        setter(x) shouldBe true
        ref.set(y)
        setter(z) shouldBe false
        ref.get() shouldBe y
      }
    }

    "tryUpdate - modification occurs successfully" {
      checkAll(Arb.int()) { x ->
        val ref = Atomic(x)
        ref.tryUpdate { it + 1 }
        ref.get() shouldBe x + 1
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

    "consistent set update" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        val set = suspend {
          val r = Atomic(x)
          r.set(y)
          r.get()
        }

        val update = suspend {
          val r = Atomic(x)
          r.update { y }
          r.get()
        }

        set() shouldBe update()
      }
    }

    "access id" {
      checkAll(Arb.int()) { x ->
        val r = Atomic(x)
        val (a, _) = r.access()
        r.get() shouldBe a
      }
    }

    "consistent access tryUpdate" {
      checkAll(Arb.int()) { x ->
        val acccessMap = suspend {
          val r = Atomic(x)
          val (a, setter) = r.access()
          setter(a + 1)
        }
        val tryUpdate = suspend {
          val r = Atomic(x)
          r.tryUpdate { it + 1 }
        }

        acccessMap() shouldBe tryUpdate()
      }
    }

    "concurrent modifications" {
      val finalValue = 50_000
      val r = Atomic(0)
      (0 until finalValue).parTraverse { r.update { it + 1 } }
      r.get() shouldBe finalValue
    }
  }
)
