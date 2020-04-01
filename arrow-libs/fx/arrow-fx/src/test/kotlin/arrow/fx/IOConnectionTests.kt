package arrow.fx

import arrow.core.test.UnitSpec
import arrow.fx.test.eq.eq
import arrow.fx.test.laws.shouldBeEq
import io.kotlintest.shouldBe
import arrow.fx.test.eq.eqK
import arrow.typeclasses.Eq

class IOConnectionTests : UnitSpec() {

  init {
    val EQ = IO.eqK<Nothing>().liftEq(Eq.any())

    "cancellation is only executed once" {
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

    "push two, pop two" {
      var effect = 0
      val initial1 = IO { effect += 1 }
      val initial2 = IO { effect += 2 }

      val c = IOConnection()
      c.push(initial1)
      c.push(initial2)
      c.pop()
      c.pop()
      c.cancel().fix().unsafeRunSync()

      effect shouldBe 0
    }

    "pop removes tokens in LIFO order" {
      var effect = 0
      val initial1 = IO { effect += 1 }
      val initial2 = IO { effect += 2 }
      val initial3 = IO { effect += 3 }

      val c = IOConnection()
      c.push(initial1)
      c.push(initial2)
      c.push(initial3)
      c.pop().shouldBeEq(initial3, EQ)
      c.pop().shouldBeEq(initial2, EQ)
      c.pop().shouldBeEq(initial1, EQ)
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
      ref.pop().shouldBeEq(IO.unit, IO.eq())

      ref.push(IO.just(Unit))
      ref.pop().shouldBeEq(IO.unit, IO.eq())
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
