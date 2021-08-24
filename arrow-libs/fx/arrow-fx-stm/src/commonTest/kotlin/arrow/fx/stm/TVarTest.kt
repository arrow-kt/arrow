package arrow.fx.stm

import arrow.fx.stm.internal.STMFrame
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import kotlin.time.ExperimentalTime

@ExperimentalTime
class TVarTest : StringSpec() {
  init {
    "unsafeRead is consistent with atomically { read }" {
      val tv = TVar.new(10)
      tv.unsafeRead() shouldBeExactly atomically { tv.read() }
    }

    "lock returns the correct value"{
      val tv = TVar.new(1)
      tv.lock(STMFrame()) shouldBeExactly 1
    }
  }
}
