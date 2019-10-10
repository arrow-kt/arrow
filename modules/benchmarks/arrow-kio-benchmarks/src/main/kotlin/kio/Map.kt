package arrow.benchmarks.effects.kio

import it.msec.kio.effect
import it.msec.kio.map
import it.msec.kio.runtime.Runtime

object Map {

  fun kioMapTest(iterations: Int, batch: Int): Long {
    val f = { x: Int -> x + 1 }
    var fx = effect { 0 }

    var j = 0
    while (j < batch) {
      fx = fx.map(f)
      j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += Runtime.unsafeRunSyncAndGet(fx)
      i += 1
    }
    return sum
  }
}
