package arrow.fx

import arrow.core.None
import arrow.core.Some
import arrow.core.none
import arrow.core.some
import io.kotlintest.shouldBe
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.milliseconds
import io.kotlintest.specs.StringSpec

class CancelableQueue : StringSpec() {
  init {
    "Offering at 0 capacity" {
      IO.fx {
        val q = !Queue.bounded<ForIO, Int>(0, IO.concurrent())
        val start = !effect { System.currentTimeMillis() }
        val res = !q.offer(1).map { it.some() }
          .waitFor(100.milliseconds, default= IO.just(none()))
        val elapsed = !effect { System.currentTimeMillis() - start }
        !effect { res shouldBe None }
        !effect { (elapsed >= 100) shouldBe true }
      }.suspended()
    }
  }
}
