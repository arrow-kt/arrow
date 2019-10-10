package arrow.benchmarks.effects.kio

import it.msec.kio.UIO
import it.msec.kio.flatMap
import it.msec.kio.just
import it.msec.kio.runtime.Runtime

object Pure {

  fun kioPureLoop(size: Int, i: Int): UIO<Int> =
    just(i).flatMap { j ->
      if (j > size) just(j) else kioPureLoop(size, j + 1)
    }

  fun unsafeKIOPureLoop(size: Int, i: Int): Int =
    Runtime.unsafeRunSyncAndGet(kioPureLoop(size, i))
}
