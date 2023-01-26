package arrow.atomic

import arrow.fx.coroutines.parMap
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class AtomicLongTest : StringSpec({

  "set get - successful" {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val r = AtomicLong(x)
      r.value = y
      r.value shouldBe y
    }
  }

  "update get - successful" {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val r = AtomicLong(x)
      r.update { y }
      r.value shouldBe y
    }
  }

  "getAndSet - successful" {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val ref = AtomicLong(x)
      ref.getAndSet(y) shouldBe x
      ref.value shouldBe y
    }
  }

  "getAndUpdate - successful" {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val ref = AtomicLong(x)
      ref.getAndUpdate { y } shouldBe x
      ref.value shouldBe y
    }
  }

  "updateAndGet - successful" {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val ref = AtomicLong(x)
      ref.updateAndGet {
        it shouldBe x
        y
      } shouldBe y
    }
  }

  "tryUpdate - modification occurs successfully" {
    checkAll(Arb.long()) { x ->
      val ref = AtomicLong(x)
      ref.tryUpdate { it + 1 }
      ref.value shouldBe x + 1
    }
  }

  "tryUpdate - should fail to update if modification has occurred" {
    checkAll(Arb.long()) { x ->
      val ref = AtomicLong(x)
      ref.tryUpdate {
        ref.update(Long::inc)
        it + 2
      } shouldBe false
    }
  }

  "consistent set update on strings" {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val set = {
        val r = AtomicLong(x)
        r.update { y }
        r.value
      }

      val update = {
        val r = AtomicLong(x)
        r.update { y }
        r.value
      }

      set() shouldBe update()
    }
  }

  "concurrent modifications" {
    val finalValue = 50_000
    val r = AtomicLong(0)
    (0 until finalValue).parMap { r.update { it + 1 } }
    r.value shouldBe finalValue
  }
})
