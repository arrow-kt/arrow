package arrow.fx.coroutines

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

class EvalOnTests : ArrowFxSpec(spec = {

  "immediate value" {
    checkAll(Arb.int()) { i ->
      evalOn(ComputationPool) {
        i
      } shouldBe i
    }
  }

  "suspend value" {
    checkAll(Arb.int()) { i ->
      evalOn(ComputationPool) {
        i.suspend()
      } shouldBe i
    }
  }

  "evalOn on the same context doesn't dispatch" {
    suspend fun onSameContext(): String =
      evalOn(ComputationPool) {
        Thread.currentThread().name
      }

    forAll { _: Int ->
      Platform.unsafeRunSync(ComputationPool) {
        val startOn = Thread.currentThread().name
        onSameContext() shouldBe startOn
        Thread.currentThread().name == startOn
      }
    }
  }

  "evalOn on the same context doesn't intercept" {
    suspend fun onComputation(ctx: CoroutineContext): String =
      evalOn(ctx) {
        Thread.currentThread().name
      }

    forAll { _: Int ->
      val interceptor = TestableContinuationInterceptor()

      Platform.unsafeRunSync(interceptor) {
        val startOn = Thread.currentThread().name
        onComputation(interceptor) shouldBe startOn
        Thread.currentThread().name shouldBe startOn
        interceptor.timesIntercepted() shouldBe 0
        true
      }
    }
  }

  "evalOn on a different context with the same ContinuationInterceptor doesn't intercept" {
    suspend fun onComputation(ctx: CoroutineContext): String =
      evalOn(ctx + CoroutineName("Different coroutine")) {
        Thread.currentThread().name
      }

    forAll { _: Int ->
      val interceptor = TestableContinuationInterceptor()

      Platform.unsafeRunSync(interceptor) {
        val startOn = Thread.currentThread().name
        onComputation(interceptor) shouldBe startOn
        Thread.currentThread().name shouldBe startOn
        interceptor.timesIntercepted() shouldBe 0
        true
      }
    }
  }

  /*
  "evalOn on a different context with a different ContinuationInterceptor does intercept" {
    suspend fun onComputation(): String =
      evalOn(IOPool) {
        Thread.currentThread().name
      }

    forAll { _: Int -> // Run this test on single thread context to guarantee name
      single.use { ctx ->
        Platform.unsafeRunSync(ctx) {
          val startOn = Thread.currentThread().name
          onComputation() shouldNotBe startOn
          Thread.currentThread().name shouldBe startOn
          true
        }
      }
    }
  } */

  "immediate exception on KotlinX Dispatchers" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val r = try {
        evalOn<Int>(coroutineContext) {
          throw e
        }
        fail("Should never reach this point")
      } catch (throwable: Throwable) {
        throwable shouldBe e
        i
      }

      r shouldBe i
    }
  }

  "suspend exception on KotlinX Dispatchers" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val r = try {
        evalOn<Int>(coroutineContext) {
          e.suspend()
        }
        fail("Should never reach this point")
      } catch (throwable: Throwable) {
        throwable shouldBe e
        i
      }

      r shouldBe i
    }
  }

  "immediate exception from wrapped KotlinX Dispatcher" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val r = try {
        evalOn<Int>(wrapperKotlinXDispatcher(coroutineContext)) {
          throw e
        }
        fail("Should never reach this point")
      } catch (throwable: Throwable) {
        throwable shouldBe e
        i
      }

      r shouldBe i
    }
  }

  "suspend exception from wrapped KotlinX Dispatcher" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val r = try {
        evalOn<Int>(wrapperKotlinXDispatcher(coroutineContext)) {
          e.suspend()
        }
        fail("Should never reach this point")
      } catch (throwable: Throwable) {
        throwable shouldBe e
        i
      }

      r shouldBe i
    }
  }

  "immediate exception from Arrow Fx Pool" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val r = try {
        evalOn<Int>(IOPool) {
          throw e
        }
        fail("Should never reach this point")
      } catch (throwable: Throwable) {
        throwable shouldBe e
        i
      }

      r shouldBe i
    }
  }

  "suspend exception from Arrow Fx Pool" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val r = try {
        evalOn<Int>(IOPool) {
          e.suspend()
        }
        fail("Should never reach this point")
      } catch (throwable: Throwable) {
        throwable shouldBe e
        i
      }

      r shouldBe i
    }
  }
})

private class TestableContinuationInterceptor : AbstractCoroutineContextElement(ContinuationInterceptor),
  ContinuationInterceptor {

  // Starting to run test always starts with an initial intercepted, so start count on -1.
  private val invocations = atomic(-1)

  companion object Key : CoroutineContext.Key<ContinuationInterceptor>

  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
    invocations.incrementAndGet()
    return continuation
  }

  fun timesIntercepted(): Int = invocations.value
}

private fun wrapperKotlinXDispatcher(context: CoroutineContext): CoroutineContext {
  val dispatcher = context[ContinuationInterceptor] as CoroutineDispatcher
  return object : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext): Boolean =
      dispatcher.isDispatchNeeded(context)

    override fun dispatch(context: CoroutineContext, block: Runnable) =
      dispatcher.dispatch(context, block)
  }
}
