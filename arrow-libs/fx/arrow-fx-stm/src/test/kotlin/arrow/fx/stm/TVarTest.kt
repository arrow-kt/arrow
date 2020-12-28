package arrow.fx.stm

import arrow.fx.coroutines.ArrowFxSpec
import kotlin.time.milliseconds
import arrow.fx.coroutines.raceN
import arrow.fx.stm.internal.STMFrame
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TVarTest : ArrowFxSpec(spec = {
  "unsafeRead is consistent with atomically { read }" {
    val tv = TVar.new(10)
    tv.unsafeRead() shouldBeExactly atomically { tv.read() }
  }
  "lock returns the correct value" {
    val tv = TVar.new(1)
    tv.lock(STMFrame()) shouldBeExactly 1
  }
  "lock blocks future reads until release is called".config(enabled = false) {
    val tv = TVar.new(5)
    val frame = STMFrame()
    tv.lock(frame) shouldBe 5

    val sleepWon = CompletableDeferred<Unit>()
    val job1 = launch {
      raceN(Dispatchers.IO, { delay(50.milliseconds); sleepWon.complete(Unit) }, { tv.unsafeRead() })
        .fold({}, { throw IllegalStateException("Lock did not lock!") })
    }
    sleepWon.await()

    val sleepWon2 = CompletableDeferred<Unit>()
    val job2 = launch {
      raceN(Dispatchers.IO, { delay(50.milliseconds); sleepWon2.complete(Unit) }, { tv.lock(STMFrame()) })
        .fold({}, { throw IllegalStateException("Lock did not lock!") })
    }
    sleepWon2.await()

    tv.release(frame, 10)
    tv.unsafeRead() shouldBeExactly 10

    job1.join() // unsafeRead lost race
    job2.join() // tv.lock lost race
  }
  "release only unlocks a TVar if it is locked and if the frame matches".config(enabled = false) {
    val tv = TVar.new(5)
    val frame = STMFrame()
    tv.lock(frame) shouldBe 5

    val sleepWon = CompletableDeferred<Unit>()
    val job1 = launch {
      raceN(Dispatchers.IO, { delay(50.milliseconds); sleepWon.complete(Unit) }, { tv.unsafeRead() })
        .fold({}, { throw IllegalStateException("Lock did not lock!") })
    }
    sleepWon.await()

    // release with an invalid frame
    tv.release(STMFrame(), 10)

    val sleepWon2 = CompletableDeferred<Unit>()
    val job2 = launch {
      raceN(Dispatchers.IO, { delay(50.milliseconds); sleepWon2.complete(Unit) }, { tv.unsafeRead() })
        .fold({}, { throw IllegalStateException("Lock did not lock!") })
    }
    sleepWon2.await()

    // release for real
    tv.release(frame, 10)
    tv.unsafeRead() shouldBeExactly 10
    // release again should be a no-op
    tv.release(frame, 20)
    tv.unsafeRead() shouldBeExactly 10

    job1.join() // unsafeRead lost race
    job2.join() // unsafeRead lost race
  }
})
