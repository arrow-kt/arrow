package arrow.atomic

import arrow.fx.coroutines.parMap
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class AtomicBooleanTest : StringSpec({

  "set get - successful" {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val r = AtomicBoolean(x)
      r.value = y
      r.value shouldBe y
    }
  }

  "update get - successful" {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val r = AtomicBoolean(x)
      r.update { y }
      r.value shouldBe y
    }
  }

  "getAndSet - successful" {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val ref = AtomicBoolean(x)
      ref.getAndSet(y) shouldBe x
      ref.value shouldBe y
    }
  }

  "getAndUpdate - successful" {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val ref = AtomicBoolean(x)
      ref.getAndUpdate { y } shouldBe x
      ref.value shouldBe y
    }
  }

  "updateAndGet - successful" {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val ref = AtomicBoolean(x)
      ref.updateAndGet {
        it shouldBe x
        y
      } shouldBe y
    }
  }

  "tryUpdate - modification occurs successfully" {
    checkAll(Arb.boolean()) { x ->
      val ref = AtomicBoolean(x)
      ref.tryUpdate { !it }
      ref.value shouldBe !x
    }
  }

  "tryUpdate - should fail to update if modification has occurred" {
    checkAll(Arb.boolean()) { x ->
      val ref = AtomicBoolean(x)
      ref.tryUpdate {
        ref.update { b -> !b }
        it
      } shouldBe false
    }
  }

  "consistent set update on strings" {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val set = {
        val r = AtomicBoolean(x)
        r.update { y }
        r.value
      }

      val update = {
        val r = AtomicBoolean(x)
        r.update { y }
        r.value
      }

      set() shouldBe update()
    }
  }
})
