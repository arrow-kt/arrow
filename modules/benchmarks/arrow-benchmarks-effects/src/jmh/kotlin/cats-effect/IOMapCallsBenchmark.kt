package arrow.benchmarks.effects

import arrow.effects.IO
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class IOMapCallsBenchmark {

  private fun test(iterations: Int, batch: Int): Long {
    val f = {x: Int -> x + 1}
    var io = IO{0}

    var j = 0
    while (j < batch) { io = io.map(f); j += 1 }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += io.unsafeRunSync()
      i += 1
    }
    return sum
  }

  @Benchmark
  fun one(): Long = test(12000, 1)

  @Benchmark
  fun batch30(): Long = test(12000 / 30, 30)

  @Benchmark
  fun batch120(): Long = test(12000 / 120, 120)

}