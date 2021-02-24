package arrow.benchmarks

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
open class MapStream {

  @Benchmark
  fun zioOne(): Long =
    arrow.benchmarks.effects.scala.zio.`MapStream$`.`MODULE$`.test(12000, 1)

  @Benchmark
  fun zioBatch30(): Long =
    arrow.benchmarks.effects.scala.zio.`MapStream$`.`MODULE$`.test(12000 / 30, 30)

  @Benchmark
  fun zioBatch120(): Long =
    arrow.benchmarks.effects.scala.zio.`MapStream$`.`MODULE$`.test(12000 / 120, 120)

  @Benchmark
  fun catsOne(): Long =
    arrow.benchmarks.effects.scala.cats.`MapStream$`.`MODULE$`.test(12000, 1)

  @Benchmark
  fun catsBatch30(): Long =
    arrow.benchmarks.effects.scala.cats.`MapStream$`.`MODULE$`.test(12000 / 30, 30)

  @Benchmark
  fun catsBatch120(): Long =
    arrow.benchmarks.effects.scala.cats.`MapStream$`.`MODULE$`.test(12000 / 120, 120)

  @Benchmark
  fun kioOne(): Long =
    arrow.benchmarks.effects.kio.MapStream.test(12000, 1)

  @Benchmark
  fun kioBatch30(): Long =
    arrow.benchmarks.effects.kio.MapStream.test(12000 / 30, 30)

  @Benchmark
  fun kioBatch120(): Long =
    arrow.benchmarks.effects.kio.MapStream.test(12000 / 120, 120)
}
