package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.EmptyCoroutineContext

class SuspendConnectionTests : StringSpec({

  "Left identity" {
    val ref = SuspendConnection()
    (ref + EmptyCoroutineContext) == ref
  }

  "Right identity" {
    val ref = SuspendConnection()
    (EmptyCoroutineContext + ref) == ref
  }

  "Making connection uncancelable" {
    val test = CoroutineName("test")
    val ref = SuspendConnection() + test
    (ref + SuspendConnection.uncancellable) shouldBe (test + SuspendConnection.uncancellable)
  }

  "Restoring cancellation" {
    val test = CoroutineName("test")
    val ref = SuspendConnection()
    val original = ref + test
    val uncancelable = original + SuspendConnection.uncancellable
    (uncancelable + original) shouldBe (test + original)
  }

  "cancellation is only executed once" {
    var effect = 0
    val initial = CancelToken { effect += 1 }
    val c = SuspendConnection()
    c.push(initial)

    c.cancel()
    effect shouldBe 1

    c.cancel()
    effect shouldBe 1
  }

  "Disposable is only executed once" {
    var effect = 0
    val initial = CancelToken { effect += 1 }
    val c = SuspendConnection()
    c.push(initial)

    c.toDisposable().invoke()
    effect shouldBe 1

    c.toDisposable().invoke()
    effect shouldBe 1
  }

  "CancelToken delays cancel effect until invoked" {
    var effect = 0
    val initial = CancelToken { effect += 1 }
    val c = SuspendConnection()
    c.push(initial)

    val token = c.cancelToken()
    effect shouldBe 0

    token.invoke()
    effect shouldBe 1
  }

  "empty; isCancelled" {
    val c = SuspendConnection()
    c.isCancelled() shouldBe false
  }

  "empty; isNotCancelled" {
    val c = SuspendConnection()
    c.isNotCancelled() shouldBe true
  }

  "empty; push; cancel; isCancelled" {
    val c = SuspendConnection()
    c.push(CancelToken {})
    c.cancel()
    c.isCancelled() shouldBe true
  }

  "cancel immediately if already cancelled" {
    var effect = 0
    val initial = CancelToken { effect += 1 }
    val c = SuspendConnection()
    c.push(initial)

    c.cancel()
    effect shouldBe 1

    c.push(initial)
    effect shouldBe 2
  }

  "push two, pop one" {
    var effect = 0
    val initial1 = CancelToken { effect += 1 }
    val initial2 = CancelToken { effect += 2 }

    val c = SuspendConnection()
    c.push(initial1)
    c.push(initial2)
    c.pop()

    c.cancel()
    effect shouldBe 1
  }

  "push two, pop two" {
    var effect = 0
    val initial1 = CancelToken { effect += 1 }
    val initial2 = CancelToken { effect += 2 }

    val c = SuspendConnection()
    c.push(initial1)
    c.push(initial2)
    c.pop()
    c.pop()
    c.cancel()

    effect shouldBe 0
  }

  "pop removes tokens in LIFO order" {
    var effect = 0
    val initial1 = CancelToken { effect += 1 }
    val initial2 = CancelToken { effect += 2 }
    val initial3 = CancelToken { effect += 3 }

    val c = SuspendConnection()
    c.push(initial1)
    c.push(initial2)
    c.push(initial3)
    c.pop() shouldBe initial3
    c.pop() shouldBe initial2
    c.pop() shouldBe initial1
  }

  "push list tokens" {
    var effect = 0
    val initial = CancelToken { effect += 1 }

    val c = SuspendConnection()
    c.push(listOf(initial, initial, initial))
    c.cancel()

    effect shouldBe 3
  }

  "pushPair token" {
    var effect = 0
    val initial1 = CancelToken { effect += 1 }
    val initial2 = CancelToken { effect += 2 }

    val c = SuspendConnection()
    c.pushPair(initial1, initial2)
    c.cancel()

    effect shouldBe 3
  }

  "pushPair connections" {
    var effect = 0
    val ref1 = SuspendConnection().apply {
      push(CancelToken { effect += 1 })
    }
    val ref2 = SuspendConnection().apply {
      push(CancelToken { effect += 2 })
    }

    val c = SuspendConnection()
    c.pushPair(ref1, ref2)
    c.cancel()

    effect shouldBe 3
  }

  "Cannot reactivate when not cancelled" {
    val ref = SuspendConnection()
    ref.tryReactivate() shouldBe false
  }

  "Cannot reactivate when cancelled" {
    val ref = SuspendConnection()
    ref.cancel()
    ref.tryReactivate() shouldBe true
  }

  "uncancellable returns same reference" {
    val ref1 = SuspendConnection.uncancellable
    val ref2 = SuspendConnection.uncancellable
    ref1 shouldBe ref2
  }

  "uncancellable reference cannot be cancelled" {
    val ref = SuspendConnection.uncancellable
    ref.isCancelled() shouldBe false

    ref.cancel()
    ref.isCancelled() shouldBe false
  }

  "uncancellable.pop" {
    val ref = SuspendConnection.uncancellable
    ref.pop() should unsafeEquals(CancelToken.unit)

    ref.push(CancelToken.unit)
    ref.pop() should unsafeEquals(CancelToken.unit)
  }

  "uncancellable.push never cancels the given cancellable" {
    val ref = SuspendConnection.uncancellable
    ref.cancel()

    var effect = 0
    val c = CancelToken { effect += 1 }
    ref.push(c)
    effect shouldBe 0
  }

  "uncancellable can always reactivate" {
    val ref = SuspendConnection.uncancellable
    ref.tryReactivate() shouldBe true
  }
})
