package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.milliseconds

class SemaphoreTest : ArrowFxSpec(spec = {

  "acquire n times synchronously" {
    checkAll(Arb.positiveInts(max = 20).map(Int::toLong)) { n ->
      val s = Semaphore(n)

      repeat(n.toInt()) {
        s.acquire()
      }

      s.available() shouldBe 0
    }
  }

  "acquireN synchronously" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.acquireN(n)
      s.available() shouldBe 0
    }
  }

  "releaseN can add permits" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.releaseN(n)
      s.available() shouldBe n * 2
    }
  }

  "releaseN hands out outstanding permits to waiting acquireN" {
    checkAll(Arb.positiveInts().map(Int::toLong), Arb.string()) { n, x ->
      val s = Semaphore(n / 2)
      val start = Promise<Unit>()
      val f = ForkAndForget {
        start.complete(Unit)
        s.acquireN(n)
        x
      }
      start.get()
      s.releaseN(n)
      f.join() shouldBe x
    }
  }

  "tryAcquire with available permits" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.acquireN(n - 1)
      s.tryAcquire() shouldBe true
    }
  }

  "tryAcquire with no available permits" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.acquireN(n)
      s.tryAcquire() shouldBe false
    }
  }

  "available with available permits" {
    val minPermits = 10
    checkAll(
      Arb.int(minPermits, Int.MAX_VALUE).map(Int::toLong),
      Arb.int(1, minPermits).map(Int::toLong)
    ) { n, x ->
      val s = Semaphore(n)
      s.acquireN(n - x)
      s.available() shouldBe x
    }
  }

  "available with no available permits" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.acquireN(n)
      s.available() shouldBe 0
    }
  }

  "tryAcquireN with no available permits" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.acquireN(n)
      s.tryAcquireN(1) shouldBe false
    }
  }

  "count with available permits" {
    val minPermits = 10
    checkAll(
      Arb.int(minPermits, Int.MAX_VALUE).map(Int::toLong),
      Arb.int(1, minPermits)
    ) { n, x ->
      val s = Semaphore(n)
      repeat(x) { s.acquire() }
      s.available() shouldBe s.count()
    }
  }

  "count with no available permits" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n ->
      val s = Semaphore(n)
      s.acquireN(n)
      s.count() shouldBe 0
    }
  }

  suspend fun testOffsettingReleasesAcquires(
    acquires: suspend (Semaphore, List<Long>) -> Unit,
    releases: suspend (Semaphore, List<Long>) -> Unit
  ): Unit {
    val permits = listOf(1L, 0, 20, 4, 0, 5, 2, 1, 1, 3)
    val s = Semaphore(0)

    parTupledN(
      { acquires(s, permits) },
      { releases(s, permits) }
    )

    s.count() shouldBe 0
  }

  "offsetting acquires/releases - acquires parallel with releases" {
    testOffsettingReleasesAcquires(
      { s, p -> p.forEach { s.acquireN(it) } },
      { s, p -> p.reversed().forEach { s.releaseN(it) } }
    )
  }

  "offsetting acquires/releases - individual acquires/increment in parallel" {
    testOffsettingReleasesAcquires(
      { s, p -> p.parTraverse { s.acquireN(it) } },
      { s, p -> p.reversed().parTraverse { s.releaseN(it) } }
    )
  }

  "Failure in withPermitN results in correct error & releases permits" {
    checkAll(Arb.positiveInts().map(Int::toLong), Arb.throwable()) { n, e ->
      val s = Semaphore(n)
      val p = Promise<Long>()
      val r = Either.catch {
        s.withPermitN(n) {
          p.complete(s.available())
          throw e
        }
      }

      r shouldBe Either.Left(e)
      p.get() shouldBe 0
      s.available() shouldBe n
    }
  }

  "withPermitN does not leak fibers or permits upon failure" {
    checkAll(Arb.positiveInts().map(Int::toLong), Arb.throwable()) { n, e ->
      val s = Semaphore(n)

      val r = Either.catch {
        parTupledN({
          s.withPermitN(n + 1) { // Requests a n + 1 permits, puts count to -1
            s.release() // Never runs due to async exception
          }
        }, { throw e }) // Cancels other parallel op
      }

      r should leftException(e) // Proof parallel op failed
      s.count() shouldBe n // Proof that withPermitN released on cancel
    }
  }

  "withPermitN does not leak fibers or permits upon cancellation" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n -> // 100 iterations takes 1 second
      val s = Semaphore(n)

      val r = withTimeoutOrNull(10.milliseconds) {
        s.withPermitN(n + 1) { // Requests a n + 1 permits, puts count to -1
          s.release() // Timeouts out due to no permit
        } // cancel should put count back to 0
      }

      r shouldBe null // proof of timeout

      // proof of permit cancel, if release ran it would be 1L
      s.count() shouldBe n
    }
  }

  "acquireN does not leak fibers or permits upon failure" {
    checkAll(Arb.positiveInts().map(Int::toLong), Arb.throwable()) { n, e ->
      val s = Semaphore(n)

      val r = Either.catch {
        parTupledN({
          s.acquireN(n + 1) // Puts count to -1, and gets cancelled by async exception
        }, { throw e })
      }

      r should leftException(e) // Proof parallel op failed
      s.count() shouldBe n // Proof that acquireN released on cancel
    }
  }

  "acquireN does not leak permits upon cancellation" {
    checkAll(Arb.positiveInts().map(Int::toLong)) { n -> // 100 iterations takes 1 second
      val s = Semaphore(n)

      val x = withTimeoutOrNull(10.milliseconds) {
        // Puts count to -1, and times out before acquired so should out count back to 1
        s.acquireN(n + 1)
        s.release() // Never runs
      }

      x shouldBe null // proof of timeout

      // proof of cancel, if release ran then would be 2L
      s.count() shouldBe n
    }
  }
})
