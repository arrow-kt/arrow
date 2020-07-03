package arrow.benchmarks

import arrow.fx.IO
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Map {

  @Benchmark
  fun zioOne(): Long =
    arrow.benchmarks.effects.scala.zio.`Map$`.`MODULE$`.zioMapTest(12000, 1)

  @Benchmark
  fun zioBatch30(): Long =
    arrow.benchmarks.effects.scala.zio.`Map$`.`MODULE$`.zioMapTest(12000 / 30, 30)

  @Benchmark
  fun zioBatch120(): Long =
    arrow.benchmarks.effects.scala.zio.`Map$`.`MODULE$`.zioMapTest(12000 / 120, 120)

  @Benchmark
  fun catsOne(): Long =
    arrow.benchmarks.effects.scala.cats.`Map$`.`MODULE$`.catsIOMapTest(12000, 1)

  @Benchmark
  fun catsBatch30(): Long =
    arrow.benchmarks.effects.scala.cats.`Map$`.`MODULE$`.catsIOMapTest(12000 / 30, 30)

  @Benchmark
  fun catsBatch120(): Long =
    arrow.benchmarks.effects.scala.cats.`Map$`.`MODULE$`.catsIOMapTest(12000 / 120, 120)

  @Benchmark
  fun ioOne(): Long = ioTest(12000, 1)

  @Benchmark
  fun ioBatch30(): Long = ioTest(12000 / 30, 30)

  @Benchmark
  fun ioBatch120(): Long = ioTest(12000 / 120, 120)

  @Benchmark
  fun kioOne(): Long =
    arrow.benchmarks.effects.kio.Map.kioMapTest(12000, 1)

  @Benchmark
  fun kioBatch30(): Long =
    arrow.benchmarks.effects.kio.Map.kioMapTest(12000 / 30, 30)

  @Benchmark
  fun kioBatch120(): Long =
    arrow.benchmarks.effects.kio.Map.kioMapTest(12000 / 120, 120)

  private fun ioTest(iterations: Int, batch: Int): Long {
    val f = { x: Int -> x + 1 }
    var io = IO.just(0)

    var j = 0
    while (j < batch) {
      io = io.map(f); j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += io.unsafeRunSync()
      i += 1
    }
    return sum
  }
}
