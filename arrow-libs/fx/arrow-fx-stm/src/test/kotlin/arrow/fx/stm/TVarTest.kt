package arrow.fx.stm

import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.raceN
import arrow.fx.coroutines.sleep
import arrow.fx.stm.internal.STMFrame
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class TVarTest : ArrowFxSpec(spec = {
  "unsafeRead is consistent with atomically { read }" {
    val tv = TVar.new(10)
    tv.unsafeRead() shouldBeExactly atomically { tv.read() }
  }
  "lock returns the correct value" {
    val tv = TVar.new(1)
    tv.lock(STMFrame()) shouldBeExactly 1
  }
  "lock blocks future reads until release is called" {
    val tv = TVar.new(5)
    val frame = STMFrame()
    tv.lock(frame) shouldBe 5
    raceN({ sleep(50.milliseconds) }, { tv.unsafeRead() })
      .fold({}, { throw IllegalStateException("Lock did not lock!") })

    raceN({ sleep(50.milliseconds) }, { tv.lock(STMFrame()) })
      .fold({}, { throw IllegalStateException("Lock did not lock!") })

    tv.release(frame, 10)
    tv.unsafeRead() shouldBeExactly 10
  }
  "release only unlocks a TVar if it is locked and if the frame matches" {
    val tv = TVar.new(5)
    val frame = STMFrame()
    tv.lock(frame) shouldBe 5
    raceN({ sleep(50.milliseconds) }, { tv.unsafeRead() })
      .fold({}, { throw IllegalStateException("Lock did not lock!") })
    // release with an invalid frame
    tv.release(STMFrame(), 10)
    raceN({ sleep(50.milliseconds) }, { tv.unsafeRead() })
      .fold({}, { throw IllegalStateException("Lock did not lock!") })
    // release for real
    tv.release(frame, 10)
    tv.unsafeRead() shouldBeExactly 10
    // release again should be a no-op
    tv.release(frame, 20)
    tv.unsafeRead() shouldBeExactly 10
  }
})
