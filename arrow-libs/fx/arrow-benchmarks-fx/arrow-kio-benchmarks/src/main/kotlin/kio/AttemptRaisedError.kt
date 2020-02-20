package arrow.benchmarks.effects.kio

import it.msec.kio.Task
import it.msec.kio.attempt
import it.msec.kio.effect
import it.msec.kio.just
import it.msec.kio.tryRecover

object AttemptRaisedError {

  private val dummy = object : RuntimeException("dummy") {
    override fun fillInStackTrace(): Throwable =
      this
  }

  private fun loopNotHappy(size: Int, i: Int): Task<Int> =
    if (i < size) {
      effect { throw dummy }
        .attempt()
        .tryRecover { loopNotHappy(size, i + 1) }
    } else
      just(1)

  fun attemptRaisedError(size: Int) =
    it.msec.kio.runtime.Runtime.unsafeRunSync(loopNotHappy(size, 0))
}
