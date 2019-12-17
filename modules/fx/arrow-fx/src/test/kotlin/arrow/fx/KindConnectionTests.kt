package arrow.fx

import arrow.test.UnitSpec
import io.kotlintest.shouldBe

class KindConnectionTests : UnitSpec() {

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

    "empty; isCanceled" {
      val c = IOConnection()
      c.isCanceled() shouldBe false
    }

    "empty; isNotCanceled" {
      val c = IOConnection()
      c.isNotCanceled() shouldBe true
    }

    "empty; push; cancel; isCanceled" {
      val c = IOConnection()
      c.push(IO {})
      c.cancel().fix().unsafeRunSync()
      c.isCanceled() shouldBe true
    }

    "cancel immediately if already canceled" {
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
      c.pop() shouldBe initial2
      c.pop() shouldBe initial1
      c.cancel().fix().unsafeRunSync()

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

    "uncancelable returns same reference" {
      val ref1 = IOConnection.uncancelable
      val ref2 = IOConnection.uncancelable
      ref1 shouldBe ref2
    }

    "uncancelable reference cannot be canceled" {
      val ref = IOConnection.uncancelable
      ref.isCanceled() shouldBe false
      ref.cancel().fix().unsafeRunSync()
      ref.isCanceled() shouldBe false
    }

    "uncancelable.pop" {
      val ref = IOConnection.uncancelable
      ref.pop() shouldBe IO.unit

      ref.push(IO.just(Unit))
      ref.pop() shouldBe IO.unit
    }

    "uncancelable.push never cancels the given cancelable" {
      val ref = IOConnection.uncancelable
      ref.cancel().fix().unsafeRunSync()

      var effect = 0
      val c = IO { effect += 1 }
      ref.push(c)
      effect shouldBe 0
    }
  }
}
