package arrow.benchmarks.effects.kio

import it.msec.kio.UIO
import it.msec.kio.flatMap
import it.msec.kio.just
import it.msec.kio.runtime.Runtime.unsafeRunSyncAndGet

object LeftBind {

  fun loop(depth: Int, size: Int, i: Int): UIO<Int> =
    when {
        i % depth == 0 -> just(i + 1).flatMap { loop(depth, size, it) }
        i < size -> loop(depth, size, i + 1).flatMap { just(it) }
        else -> just(i)
    }

  fun leftBind(depth: Int, size: Int, i: Int) =
    unsafeRunSyncAndGet(loop(depth, size, i))
}
