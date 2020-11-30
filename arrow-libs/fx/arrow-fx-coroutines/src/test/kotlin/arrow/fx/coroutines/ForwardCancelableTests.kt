package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

class ForwardCancelableTests : ArrowFxSpec(spec = {
  "cancel() after complete" {
    var effect = 0

    val ref = ForwardCancellable(coroutineContext)
    ref.complete(CancelToken { effect += 1 })
    effect shouldBe 0

    ref.cancel()
    effect shouldBe 1

    // Weak idempotency guarantees (not thread-safe)
    ref.cancel()
    effect shouldBe 1
  }

  "cancel() before complete" {
    var effect = 0

    val ref = ForwardCancellable(coroutineContext)
    ref::cancel.startCoroutine(Continuation(EmptyCoroutineContext) { })
    effect shouldBe 0

    ref.complete(CancelToken { effect += 1 })
    effect shouldBe 1

    shouldThrow<ArrowInternalException> { ref.complete(CancelToken { effect += 2 }) }
    // completed task was canceled before error was thrown
    effect shouldBe 3

    ref.cancel()
    effect shouldBe 3
  }

  "complete twice before cancel" {
    var effect = 0

    val ref = ForwardCancellable(coroutineContext)
    ref.complete(CancelToken { effect += 1 })
    effect shouldBe 0

    shouldThrow<ArrowInternalException> { ref.complete(CancelToken { effect += 2 }) }
    effect shouldBe 2

    ref.cancel()
    effect shouldBe 3
  }
})
