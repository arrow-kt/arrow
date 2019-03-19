package arrow.benchmarks

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.effects.IO
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class MapStream {

  private fun streamTest(times: Int, batchSize: Int): Long {
    var stream = range(0, times)
    var i = 0
    while (i < batchSize) {
      stream = mapStream(stream){ x: Int -> x + 1 }
      i += 1
    }
    return sum(0, stream).unsafeRunSync()
  }

  data class Stream(val value: Int, val next: IO<Option<Stream>>)

  private fun range(from: Int, until: Int): Option<Stream> =
    if (from < until)
      Some(Stream(from, IO { range(from + 1, until) }))
    else None

  private fun mapStream(box: Option<Stream>, f: (Int) -> Int): Option<Stream> =
    when (box) {
      is Some -> Some(Stream(f(box.t.value), box.t.next.map { mapStream(it, f) }))
      is None -> None
    }

  private fun sum(acc: Long, box: Option<Stream>): IO<Long> =
    when (box) {
      is Some -> box.t.next.flatMap{sum(acc + box.t.value, it)}
      is None -> IO.just(acc)
    }

  @Benchmark
  fun ioOne(): Long = streamTest(12000, 1)

  @Benchmark
  fun ioBatch30(): Long = streamTest(1000, 30)

  @Benchmark
  fun ioBatch120(): Long = streamTest(100, 120)

}