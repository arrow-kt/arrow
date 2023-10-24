package arrow.atomic

import arrow.fx.coroutines.parMap
import io.kotest.common.runBlocking
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class AtomicTest {

  @Test
  fun setGetSuccessful() = runTest {
    checkAll(Arb.string(), Arb.string()) { x, y ->
      val r = Atomic(x)
      r.value = y
      r.value shouldBe y
    }
  }

  @Test
  fun updateGetSuccessful() = runTest {
    checkAll(Arb.string(), Arb.string()) { x, y ->
      val r = Atomic(x)
      r.update { y }
      r.value shouldBe y
    }
  }

  @Test
  fun getAndSetSuccessful() = runTest {
    checkAll(Arb.string(), Arb.string()) { x, y ->
      val ref = Atomic(x)
      ref.getAndSet(y) shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun getAndUpdateSuccessful() = runTest {
    checkAll(Arb.string(), Arb.string()) { x, y ->
      val ref = Atomic(x)
      ref.getAndUpdate { y } shouldBe x
      ref.value shouldBe y
    }
  }

  @Test
  fun updateAndGetSuccessful() = runTest {
    checkAll(Arb.string(), Arb.string()) { x, y ->
      val ref = Atomic(x)
      ref.updateAndGet {
        it shouldBe x
        y
      } shouldBe y
    }
  }

  @Test
  fun tryUpdateModificationOccursSuccessfully() = runTest {
    checkAll(Arb.string()) { x ->
      val ref = Atomic(x)
      ref.tryUpdate { it + 1 }
      ref.value shouldBe x + 1
    }
  }

  @Test
  fun tryUpdateShouldFailToUpdateIfModificationHasOccurred() = runTest {
    checkAll(Arb.string()) { x ->
      val ref = Atomic(x)
      ref.tryUpdate {
        suspend { ref.update { it + "a" } }
          .startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { })
        it + "b"
      } shouldBe false
    }
  }

  @Test
  fun consistentSetUpdateOnStrings() = runTest {
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

  @Test
  fun concurrentModifications() = runBlockingOnNative {
    val finalValue = 50_000
    val r = Atomic("")
    (0 until finalValue).parMap { r.update { it + "a" } }
    r.value shouldBe "a".repeat(finalValue)
  }
}
