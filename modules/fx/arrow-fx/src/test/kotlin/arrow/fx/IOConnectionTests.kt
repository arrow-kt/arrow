package arrow.fx

import arrow.test.UnitSpec
import io.kotlintest.shouldBe

class IOConnectionTests : UnitSpec() {

  init {
    "initial push" {
      var effect = 0
      val initial = IO { effect += 1 }
      val c = IOConnection()
      c.push(initial)
      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1
      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1
    }

    "empty; isCancelled" {
      val c = IOConnection()
      c.isCancelled() shouldBe false
    }

    "empty; isNotCancelled" {
      val c = IOConnection()
      c.isNotCancelled() shouldBe true
    }

    "empty; push; cancel; isCancelled" {
      val c = IOConnection()
      c.push(IO {})
      c.cancel().fix().unsafeRunSync()
      c.isCancelled() shouldBe true
    }

    "cancel immediately if already cancelled" {
      var effect = 0
      val initial = IO { effect += 1 }
      val c = IOConnection()
      c.push(initial)

      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1

      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1

      c.push(initial)
      effect shouldBe 2
    }

    "push two, pop one" {
      var effect = 0
      val initial1 = IO { effect += 1 }
      val initial2 = IO { effect += 2 }

      val c = IOConnection()
      c.push(initial1)
      c.push(initial2)
      c.pop()

      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1
    }

    "cancel the second time is a no-op" {
      var effect = 0
      val bc = IO { effect += 1 }
      val c = IOConnection()
      c.push(bc)

      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1
      c.cancel().fix().unsafeRunSync()
      effect shouldBe 1
    }

    "push two, pop two" {
      var effect = 0
      val initial1 = IO { effect += 1 }
      val initial2 = IO { effect += 2 }

      val c = IOConnection()
      c.push(initial1)
      c.push(initial2)
      c.pop()
      c.pop()
      c.cancel().unsafeRunSync()

      effect shouldBe 0
    }

    "pushPair" {
      var effect = 0
      val initial1 = IO { effect += 1 }
      val initial2 = IO { effect += 2 }

      val c = IOConnection()
      c.pushPair(initial1, initial2)
      c.cancel().fix().unsafeRunSync()

      effect shouldBe 3
    }

    "uncancellable returns same reference" {
      val ref1 = IOConnection.uncancellable
      val ref2 = IOConnection.uncancellable
      ref1 shouldBe ref2
    }

    "uncancellable reference cannot be cancelled" {
      val ref = IOConnection.uncancellable
      ref.isCancelled() shouldBe false
      ref.cancel().fix().unsafeRunSync()
      ref.isCancelled() shouldBe false
    }

    "uncancellable.pop" {
      val ref = IOConnection.uncancellable
      ref.pop() shouldBe IO.unit

      ref.push(IO.just(Unit))
      ref.pop() shouldBe IO.unit
    }

    "uncancellable.push never cancels the given cancellable" {
      val ref = IOConnection.uncancellable
      ref.cancel().fix().unsafeRunSync()

      var effect = 0
      val c = IO { effect += 1 }
      ref.push(c)
      effect shouldBe 0
    }
  }
}
