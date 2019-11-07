package arrow.benchmarks.effects.kio

import it.msec.kio.Task
import it.msec.kio.effect
import it.msec.kio.flatMap
import it.msec.kio.map
import it.msec.kio.just
import it.msec.kio.result.getOrThrow
import it.msec.kio.runtime.Runtime.unsafeRunSync

object MapStream {

  fun test(times: Int, batchSize: Int): Long {
    var stream = range(0, times)
    var i = 0
    while (i < batchSize) {
      stream = mapStream(addOne)(stream)
      i += 1
    }

    return unsafeRunSync(sum(0)(stream)).getOrThrow()
  }

  data class Stream(val value: Int, val next: Task<Stream?>)

  val addOne = { x: Int -> x + 1 }

  fun range(from: Int, until: Int): Stream? =
    if (from < until)
      Stream(from, effect { range(from + 1, until) })
    else
      null

  fun mapStream(f: (Int) -> Int): (box: Stream?) -> Stream? = { box ->
    if (box != null)
      Stream(f(box.value), box.next.map(mapStream(f)))
    else
      null
  }

  fun sum(acc: Long): (Stream?) -> Task<Long> = { box ->
    box?.next?.flatMap(sum(acc + box.value)) ?: just(acc)
  }
}
