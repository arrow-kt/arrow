package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalStateException
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

class ForwardCancelableTests : StringSpec({
  "cancel() after complete" {
    var effect = 0

    val ref = ForwardCancellable()
    ref.complete(CancelToken { effect += 1 })
    effect shouldBe 0

    Platform.unsafeRunSync(ref.cancel().cancel)
    effect shouldBe 1

    // Weak idempotency guarantees (not thread-safe)
    Platform.unsafeRunSync(ref.cancel().cancel)
    effect shouldBe 1
  }

  "cancel() before complete" {
    var effect = 0

    val ref = ForwardCancellable()
    ref.cancel().cancel.startCoroutine(Continuation(EmptyCoroutineContext) { })
    effect shouldBe 0

    ref.complete(CancelToken { effect += 1 })
    effect shouldBe 1

    shouldThrow<IllegalStateException> { ref.complete(CancelToken { effect += 2 }) }
    // completed task was canceled before error was thrown
    effect shouldBe 3

    Platform.unsafeRunSync(ref.cancel().cancel)
    effect shouldBe 3
  }

  "complete twice before cancel" {
    var effect = 0

    val ref = ForwardCancellable()
    ref.complete(CancelToken { effect += 1 })
    effect shouldBe 0

    shouldThrow<IllegalStateException> { ref.complete(CancelToken { effect += 2 }) }
    effect shouldBe 2

    Platform.unsafeRunSync(ref.cancel().cancel)
    effect shouldBe 3
  }
})
