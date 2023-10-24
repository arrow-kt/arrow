package arrow.atomic

import arrow.fx.coroutines.parMap
import io.kotest.common.runBlocking
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class AtomicLongTest {

  @Test
  fun setGetSuccessful() = runTest {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val r = AtomicLong(x)
      r.value = y
      r.value shouldBe y
    }
  }

  @Test
  fun updateGetSuccessful() = runTest {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val r = AtomicLong(x)
      r.update { y }
      r.value shouldBe y
    }
  }

  @Test
  fun getAndSetSuccessful() = runTest {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val ref = AtomicLong(x)
      ref.getAndSet(y) shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun getAndUpdateSuccessful() = runTest {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val ref = AtomicLong(x)
      ref.getAndUpdate { y } shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun updateAndGetSuccessful() = runTest {
    checkAll(Arb.long(), Arb.long()) { x, y ->
      val ref = AtomicLong(x)
      ref.updateAndGet {
        it shouldBe x
        y
      } shouldBe y
    }
  }

  @Test
  fun tryUpdateModificationOccursSuccessfully() = runTest {
    checkAll(Arb.long()) { x ->
      val ref = AtomicLong(x)
      ref.tryUpdate { it + 1 }
      ref.value shouldBe x + 1
    }
  }

  @Test
  fun tryUpdateShouldFailToUpdateIfModificationHasOccurred() = runTest {
    checkAll(Arb.long()) { x ->
      val ref = AtomicLong(x)
      ref.tryUpdate {
        ref.update(Long::inc)
        it + 2
      } shouldBe false
    }
  }

  @Test
  fun consistentSetUpdateOnStrings() = runTest {
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

  @Test
  fun concurrentModifications() = runBlockingOnNative {
    val finalValue = 50_000
    val r = AtomicLong(0)
    (0 until finalValue).parMap { r.update { it + 1 } }
    r.value shouldBe finalValue
  }
}
