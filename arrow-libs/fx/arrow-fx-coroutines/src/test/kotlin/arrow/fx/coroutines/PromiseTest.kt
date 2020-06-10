package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PromiseTest : StringSpec({

  class MyException : Exception()

  val exception = MyException()

  "complete" {
    val p = Promise<Int>()
    p.complete(1) shouldBe Either.Right(Unit)
    p.get() shouldBe 1
  }

  "complete twice should result in Promise.AlreadyFulfilled" {
    val p = Promise<Int>()
    p.complete(1) shouldBe Either.Right(Unit)
    p.complete(2) shouldBe Either.Left(Promise.AlreadyFulfilled)
    p.get() shouldBe 1
  }

  "get blocks until set" {
    val r = Atomic(0)
    val modifyGate = Promise<Int>()
    val readGate = Promise<Int>()

    ForkAndForget {
      modifyGate.get()
      r.update { i -> i * 2 }
      readGate.complete(0)
    }

    ForkAndForget {
      r.set(1)
      modifyGate.complete(0)
    }

    readGate.get()
    r.get() shouldBe 2
  }

  "tryGet returns None for empty Promise" {
    Promise<Int>().tryGet() shouldBe null
  }

  "tryGet returns Some for completed promise" {
    val p = Promise<Int>()
    p.complete(1)
    p.tryGet() shouldBe 1
  }
})
