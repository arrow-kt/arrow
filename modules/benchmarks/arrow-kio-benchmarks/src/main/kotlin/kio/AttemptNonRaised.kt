package arrow.benchmarks.effects.kio

import it.msec.kio.Task
import it.msec.kio.attempt
import it.msec.kio.effect
import it.msec.kio.just
import it.msec.kio.recover
import it.msec.kio.flatMap
import it.msec.kio.result.getOrThrow
import it.msec.kio.runtime.Runtime

object AttemptNonRaised {

  fun attemptNonRaised(size: Int): Int = Runtime.unsafeRunSync(loopHappy(size, 0)).getOrThrow()

  fun loopHappy(size: Int, i: Int): Task<Int> =
    if (i < size) {
      effect { i + 1 }
        .attempt()
        .recover { throw RuntimeException("not happening") }
        .flatMap { loopHappy(size, it) }
    } else
      just(1)
}
