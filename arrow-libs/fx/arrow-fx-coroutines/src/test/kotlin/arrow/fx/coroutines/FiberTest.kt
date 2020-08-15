package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class FiberTest : ArrowFxSpec(spec = {

  "ForkConnected returns on the original context" {
    val forkCtxName = "forkCtx"
    val forker = Resource.singleThreadContext(forkCtxName)
    checkAll(Arb.int()) { i ->
      single.zip(forker).use { (single, forker) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val f = ForkConnected(forker) { Pair(i.suspend(), threadName()) }

          f.join() shouldBe Pair(i, forkCtxName)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "ForkConnected returns on the original context on failure" {
    val forkCtxName = "forkCtx"
    val forker = Resource.singleThreadContext(forkCtxName)
    checkAll(Arb.throwable()) { e ->
      single.zip(forker).use { (single, forker) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val f = ForkConnected(forker) { e.suspend() }

          Either.catch { f.join() } shouldBe Either.Left(e)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "ForkConnected get cancelled by its parent" {
    val start = Promise<Unit>()
    val p = Promise<ExitCase>()

    val parent = ForkAndForget {
      ForkConnected { // Child fiber
        guaranteeCase({
          start.complete(Unit)
          never<Unit>()
        }, { p.complete(it) })
      }
    }

    start.get()
    parent.cancel()
    p.get() shouldBe ExitCase.Cancelled
  }

  "ForkConnected doesn't cancel its parent" {
    checkAll(Arb.int()) { i ->
      val cancelled = Promise<Unit>()

      val parent = ForkAndForget {
        val f = ForkConnected { never<Unit>() }
        parTupledN({ cancelled.get() }, { f.cancel(); cancelled.complete(Unit) })
        cancelBoundary()
        i
      }

      parent.join() shouldBe i
    }
  }

  "ForkConnected runs on the expected dispatcher" {
    suspend fun threadName(): String =
      Thread.currentThread().name

    single.use { ctx ->
      val fiber = ::threadName.forkConnected(ctx)

      // Join fiber before closing ctx resource
      fiber.join() shouldBe "single"
    }
  }

  "ForkConnected.join is idempotent" {
    checkAll(Arb.int()) { i ->
      val ref = Atomic(0)

      val f = suspend { ref.update { it + i } }
        .forkConnected()

      f.join()

      f.join()

      ref.get() shouldBe i
    }
  }

  "ForkConnected error join is identity" {
    checkAll(Arb.throwable()) { e ->
      val f = suspend { throw e }.forkConnected()
      Either.catch { f.join() } shouldBe Either.Left(e)
    }
  }

  "ForkConnected error cancel is unit" {
    checkAll(Arb.throwable()) { e ->
      val f = suspend { throw e }.forkConnected()
      f.cancel() shouldBe Unit
    }
  }

  "ForkConnected value cancel is unit" {
    checkAll(Arb.int()) { i ->
      val f = suspend { i }.forkConnected()
      f.cancel() shouldBe Unit
    }
  }

  "ForkScoped returns on the original context" {
    val forkCtxName = "forkCtx"
    val forker = Resource.singleThreadContext(forkCtxName)

    checkAll(Arb.int()) { i ->
      single.zip(forker).use { (single, forker) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val f = ForkScoped(forker, { never() }) { Pair(i.suspend(), threadName()) }

          f.join() shouldBe Pair(i, forkCtxName)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "ForkScoped get cancelled by interrupt token" {
    val start = Promise<Unit>()
    val p = Promise<ExitCase>()
    val interrupt = Promise<Unit>()

    ForkScoped(interruptWhen = interrupt::get) { // Child fiber
      guaranteeCase({
        start.complete(Unit)
        never<Unit>()
      }, { p.complete(it) })
    }

    start.get()
    interrupt.complete(Unit)
    p.get() shouldBe ExitCase.Cancelled
  }

  "ForkScoped runs on the expected dispatcher" {
    suspend fun threadName(): String =
      Thread.currentThread().name

    single.use { ctx ->
      val fiber = ::threadName.forkScoped(ctx) { never<Unit>() }

      // Join fiber before closing ctx resource
      fiber.join() shouldBe "single"
    }
  }

  "ForkScoped.join is idempotent" {
    checkAll(Arb.int()) { i ->
      val ref = Atomic(0)

      val f = suspend { ref.update { it + i } }
        .forkScoped { never<Unit>() }

      f.join()

      f.join()

      ref.get() shouldBe i
    }
  }

  "ForkScoped error join is identity" {
    checkAll(Arb.throwable()) { e ->
      val f = suspend { throw e }.forkScoped { never<Unit>() }
      Either.catch { f.join() } shouldBe Either.Left(e)
    }
  }

  "ForkScoped error cancel is unit" {
    checkAll(Arb.throwable()) { e ->
      val f = suspend { throw e }.forkScoped { never<Unit>() }
      f.cancel() shouldBe Unit
    }
  }

  "ForkScoped value cancel is unit" {
    checkAll(Arb.int()) { i ->
      val f = suspend { i }.forkScoped { never<Unit>() }
      f.cancel() shouldBe Unit
    }
  }

  "ForkAndForget returns on the original context" {
    val forkCtxName = "forkCtx"
    val forker = Resource.singleThreadContext(forkCtxName)

    checkAll(Arb.int()) { i ->
      single.zip(forker).use { (single, forker) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val f = ForkAndForget(forker) { Pair(i.suspend(), threadName()) }

          f.join() shouldBe Pair(i, forkCtxName)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "ForkAndForget runs on the expected dispatcher" {
    suspend fun threadName(): String =
      Thread.currentThread().name

    single.use { ctx ->
      val fiber = ::threadName.forkAndForget(ctx)

      // Join fiber before closing ctx resource
      fiber.join() shouldBe "single"
    }
  }

  "ForkAndForget.join is idempotent" {
    checkAll(Arb.int()) { i ->
      val ref = Atomic(0)

      val f = suspend { ref.update { it + i } }
        .forkAndForget()

      f.join()

      f.join()

      ref.get() shouldBe i
    }
  }

  "ForkAndForget error join is identity" {
    checkAll(Arb.throwable()) { e ->
      val f = suspend { throw e }.forkAndForget()
      Either.catch { f.join() } shouldBe Either.Left(e)
    }
  }

  "ForkAndForget error cancel is unit" {
    checkAll(Arb.throwable()) { e ->
      val f = suspend { throw e }.forkAndForget()
      f.cancel() shouldBe Unit
    }
  }

  "ForkAndForget value cancel is unit" {
    checkAll(Arb.int()) { i ->
      val f = suspend { i }.forkAndForget()
      f.cancel() shouldBe Unit
    }
  }
})
