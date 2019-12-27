package arrow.benchmarks.effects.kio

import it.msec.kio.UIO
import it.msec.kio.effect
import it.msec.kio.flatMap
import it.msec.kio.runtime.Runtime

object Delay {

  private fun kioDelayLoop(size: Int, i: Int): UIO<Int> =
    effect { i }.flatMap { j ->
      if (j > size) effect { j } else kioDelayLoop(size, j + 1)
    }

  fun unsafeIODelayLoop(size: Int, i: Int): Int =
    Runtime.unsafeRunSyncAndGet(kioDelayLoop(size, i))
}
