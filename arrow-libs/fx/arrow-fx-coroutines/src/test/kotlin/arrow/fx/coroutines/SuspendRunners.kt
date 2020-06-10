package arrow.fx.coroutines

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

class SuspendRunners : StringSpec({

  "should defer evaluation until run" {
    var run = false
    val fa = suspend { run = true }
    run shouldBe false
    Platform.unsafeRunSync(fa)
    run shouldBe true
  }

  "invoke is called on every run call" {
    val sideEffect = SideEffect()
    val fa = suspend { sideEffect.increment(); 1 }

    Platform.unsafeRunSync(fa)
    Platform.unsafeRunSync(fa)

    sideEffect.counter shouldBe 2
  }

  "should catch exceptions within main block" {
    checkAll(Arb.throwable()) { e ->
      val task = suspend { throw e }
      shouldThrow<Throwable> {
        Platform.unsafeRunSync { task.invoke() }
      } shouldBe e
    }
  }

  "should yield immediate successful invoke value" {
    checkAll(Arb.int()) { i ->
      val task = suspend { i }
      val run = Platform.unsafeRunSync { task.invoke() }
      run shouldBe i
    }
  }

  "should return a null value from unsafeRunSync" {
    val run = Platform.unsafeRunSync { suspend { null }() }
    run shouldBe null
  }

  "suspend with unsafeRunSync" {
    checkAll(Arb.int().map { suspend { it } }) { i ->
      val map = suspend { i() + 1 }
      Platform.unsafeRunSync(map) shouldBe (i.invoke() + 1)
    }
  }

  "should complete when running a pure value with unsafeRunAsync" {
    checkAll(Arb.int()) { i ->
      val task = suspend { i }

      task.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
        res.getOrThrow() shouldBe i
      })
    }
  }

  "should return an error when running an exception with unsafeRunAsync" {
    checkAll(Arb.throwable()) { e ->
      val task = suspend { throw e }
      task.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
        res.fold({ fail("Expected $e but found with $it") }, {
          it shouldBe e
        })
      })
    }
  }

  "should rethrow exceptions within run block with unsafeRunAsync" {
    checkAll(Arb.throwable()) { e ->
      val task = suspend { throw e }
      try {
        task.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
          res.fold({ fail("Expected $e but found with $it") }, { throw e })
        })
        fail("Should rethrow the exception")
      } catch (t: Throwable) {
        t shouldBe e
      }
    }
  }

  "should complete when running a pure value with startCoroutineCancellable" {
    checkAll(Arb.int()) { i ->
      val task = suspend { i }
      task.startCoroutineCancellable(CancellableContinuation(EmptyCoroutineContext) { res ->
        res.getOrThrow() shouldBe i
      })
    }
  }

  "should return exceptions within main block with startCoroutineCancellable" {
    checkAll(Arb.throwable()) { e ->
      val task = suspend { throw e }
      task.startCoroutineCancellable(CancellableContinuation(EmptyCoroutineContext) { res ->
        res.fold({ fail("Expected $e but found with $it") }, { it shouldBe e })
      })
    }
  }

  "should rethrow exceptions within run block with startCoroutineCancellable" {
    checkAll(Arb.throwable()) { e ->
      val task = suspend { throw e }
      try {
        task.startCoroutineCancellable(CancellableContinuation(EmptyCoroutineContext) { res ->
          res.fold({ fail("Expected $e but found with $it") }, { throw it })
        })
        fail("Should rethrow the exception")
      } catch (t: Throwable) {
        t shouldBe e
      }
    }
  }

  "Effect-full stack-safe map" {
    val max = 10000

    suspend fun addOne(n: Int): Int = n + 1 // Equivalent of `map` for `IO`.

    suspend fun fa(): Int =
      (0 until (max * 10000)).fold(0) { acc, _ -> addOne(acc) }

    Platform.unsafeRunSync { fa() } shouldBe (max * 10000)
  }
})
