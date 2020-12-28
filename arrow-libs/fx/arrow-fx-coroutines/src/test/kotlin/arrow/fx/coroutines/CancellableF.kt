package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.milliseconds

class CancellableF : ArrowFxSpec(spec = {

  "cancelable works for immediate values" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { res ->
      Either.catch {
        cancellable<Int> { cb ->
          res.fold(
            { e -> cb(Result.failure(e)) },
            { a -> cb(Result.success(a)) }
          )
          CancelToken.unit
        }
      } should either(res)
    }
  }

  "cancelable gets canceled" {
    val cancelled = Promise<Boolean>()
    val latch = UnsafePromise<Unit>()

    val c = launch {
      cancellable<Unit> {
        latch.complete(Result.success(Unit))
        CancelToken { cancelled.complete(true) }
      }
    }

    latch.join()
    c.cancel()

    cancelled.get() shouldBe true
  }

  "cancelableF works for immediate values" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { res ->
      val res = Either.catch {
        immediateValues(res)
      }
      res should either(res)
    }
  }

  "cancelableF works for async values" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { res ->
      val res = Either.catch {
        asyncValues(res)
      }
      res should either(res)
    }
  }

  "cancelableF can yield cancelable tasks" {
    checkAll(Arb.int()) { i ->
      val d = ConcurrentVar.empty<Int>()
      val latch = Promise<Unit>()
      val fiber = launch {
        cancellableF<Unit> { _ ->
          latch.complete(Unit)
          CancelToken { d.put(i) }
        }
      }

      latch.get()
      d.tryTake() shouldBe null
      fiber.cancel()
      d.take() shouldBe i
    }
  }

  "cancelableF executes generated task uninterruptedly" {
    checkAll(Arb.int()) { i ->
      val latch = Promise<Unit>()
      val start = Promise<Unit>()
      val done = Promise<Int>()

      val task = suspend {
        cancellableF<Unit> { cb ->
          latch.complete(Unit)
          start.get()
          cancelBoundary()
          cb(Result.success(Unit))
          done.complete(i)
          CancelToken.unit
        }
      }

      val p = UnsafePromise<Unit>()

      val cancel = task.startCoroutineCancellable(CancellableContinuation { r -> p.complete(r) })

      latch.get()

      ForkConnected { cancel.invoke() }

      // Let cancel schedule
      delay(10.milliseconds)

      start.complete(Unit) // Continue cancellableF

      done.get() shouldBe i
      p.tryGet() shouldBe null
    }
  }
})

suspend fun immediateValues(e: Either<Throwable, Int>): Int =
  cancellableF { cb ->
    e.fold(
      { e -> cb(Result.failure(e)) },
      { i -> cb(Result.success(i)) }
    )
    CancelToken.unit
  }

suspend fun asyncValues(e: Either<Throwable, Int>): Int =
  cancellableF { cb ->
    val res = e.suspend()
    res.fold(
      { e -> cb(Result.failure(e)) },
      { i -> cb(Result.success(i)) }
    )
    CancelToken.unit
  }
