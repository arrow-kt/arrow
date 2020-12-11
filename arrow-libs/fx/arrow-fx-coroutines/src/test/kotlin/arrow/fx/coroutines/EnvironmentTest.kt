package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class EnvironmentTest : StringSpec({

  "unsafeRunSync can run immediate task" {
    checkAll(Arb.int()) { i ->
      val p = UnsafePromise<Int>()
      val env = Environment()
      env.unsafeRunSync { p.complete(Result.success(i)) }
      p.join() shouldBe i
    }
  }

  "unsafeRunSync can run suspending task" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      val env = Environment()
      env.unsafeRunSync {
        Unit.suspend()
        p.complete(i)
      }
      p.get() shouldBe i
    }
  }

  "unsafeRunAsync can run immediate task" {
    checkAll(Arb.int()) { i ->
      val p = UnsafePromise<Int>()
      val env = Environment()
      env.unsafeRunAsync { p.complete(Result.success(i)) }
      p.join() shouldBe i
    }
  }

  "unsafeRunAsync can run suspending task" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      val env = Environment()
      env.unsafeRunAsync {
        Unit.suspend()
        p.complete(i)
      }
      p.get() shouldBe i
    }
  }

  "unsafeRunAsyncCancellable can run immediate task" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      val env = Environment()
      env.unsafeRunAsyncCancellable { p.complete(i) }
      p.get() shouldBe i
    }
  }

  "unsafeRunAsyncCancellable can run suspending task" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      val env = Environment()
      env.unsafeRunAsyncCancellable {
        Unit.suspend()
        p.complete(i)
      }
      p.get() shouldBe i
    }
  }

  tailrec suspend fun sleeper(): Unit {
    sleep(1.milliseconds)
    sleeper()
  }

  "unsafeRunAsyncCancellable can get cancelled independent of Environment" {
    val p = Promise<ExitCase>()
    val startLatch = Promise<Unit>()

    val env = Environment()

    val d = env.unsafeRunAsyncCancellable {
      guaranteeCase({
        startLatch.complete(Unit)
        sleeper()
      }) { ex ->
        p.complete(ex)
      }
    }

    startLatch.get()
    d.invoke()
    p.get().shouldBeInstanceOf<ExitCase.Cancelled>()
  }
})
