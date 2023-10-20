package arrow.atomic

import arrow.fx.coroutines.parMap
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class AtomicIntTest {

  @Test
  fun setGetSuccessful() = runTest {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val r = AtomicInt(x)
      r.value = y
      r.value shouldBe y
    }
  }

  @Test
  fun updateGetSuccessful() = runTest {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val r = AtomicInt(x)
      r.update { y }
      r.value shouldBe y
    }
  }

  @Test
  fun getAndSetSuccessful() = runTest {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val ref = AtomicInt(x)
      ref.getAndSet(y) shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun getAndUpdateSuccessful() = runTest {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val ref = AtomicInt(x)
      ref.getAndUpdate { y } shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun updateAndGetSuccessful() = runTest {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val ref = AtomicInt(x)
      ref.updateAndGet {
        it shouldBe x
        y
      } shouldBe y
    }
  }

  @Test
  fun tryUpdateModificationOccursSuccessfully() = runTest {
    checkAll(Arb.int()) { x ->
      val ref = AtomicInt(x)
      ref.tryUpdate { it + 1 }
      ref.value shouldBe x + 1
    }
  }

  @Test
  fun tryUpdateShouldFailToUpdateIfModificationHasOccurred() = runTest {
    checkAll(Arb.int()) { x ->
      val ref = AtomicInt(x)
      ref.tryUpdate {
        ref.update(Int::inc)
        it + 2
      } shouldBe false
    }
  }

  @Test
  fun consistentSetUpdateOnStrings() = runTest {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val set = {
        val r = AtomicInt(x)
        r.update { y }
        r.value
      }

      val update = {
        val r = AtomicInt(x)
        r.update { y }
        r.value
      }

      set() shouldBe update()
    }
  }

  @Test
  fun concurrentModifications() = runTest {
    val finalValue = 50_000
    val r = AtomicInt(0)
    (0 until finalValue).parMap { r.update { it + 1 } }
    r.value shouldBe finalValue
  }
}
