package arrow.atomic

import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.checkAll

class AtomicBooleanTest {

  @Test
  fun setGetSuccessful() = runTest {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val r = AtomicBoolean(x)
      r.value = y
      r.value shouldBe y
    }
  }

  @Test
  fun updateGetSuccessful() = runTest {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val r = AtomicBoolean(x)
      r.update { y }
      r.value shouldBe y
    }
  }

  @Test
  fun getAndSetSuccessful() = runTest {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val ref = AtomicBoolean(x)
      ref.getAndSet(y) shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun getAndUpdateSuccessful() = runTest {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val ref = AtomicBoolean(x)
      ref.getAndUpdate { y } shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun updateAndGetSuccessful() = runTest {
    checkAll(Arb.boolean(), Arb.boolean()) { x, y ->
      val ref = AtomicBoolean(x)
      ref.updateAndGet {
        it shouldBe x
        y
      } shouldBe y
    }
  }

  @Test
  fun tryUpdateModificationOccursSuccessfully() = runTest {
    checkAll(Arb.boolean()) { x ->
      val ref = AtomicBoolean(x)
      ref.tryUpdate { !it }
      ref.value shouldBe !x
    }
  }

  @Test
  fun tryUpdateShouldFailToUpdateIfModificationHasOccurred() = runTest {
    checkAll(Arb.boolean()) { x ->
      val ref = AtomicBoolean(x)
      ref.tryUpdate {
        ref.update { b -> !b }
        it
      } shouldBe false
    }
  }

  @Test
  fun consistentSetUpdateOnStrings() = runTest {
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
}
