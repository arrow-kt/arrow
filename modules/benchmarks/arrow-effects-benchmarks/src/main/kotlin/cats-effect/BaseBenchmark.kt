package arrow.benchmarks.effects

import arrow.core.flatMap
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
open class BaseBenchmark {

  @Benchmark
  fun hello(): Int = 0

}
