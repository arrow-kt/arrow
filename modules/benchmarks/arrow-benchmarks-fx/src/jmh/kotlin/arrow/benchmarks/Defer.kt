package arrow.benchmarks

import arrow.fx.IO
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Defer {

  @Param("3000")
  var size: Int = 0

  private fun ioDeferLoop(i: Int): IO<Int> =
    IO.defer { IO.just(i) }.flatMap { j ->
      if (j > size) IO.defer { IO.just(j) } else ioDeferLoop(j + 1)
    }

  @Benchmark
  fun io(): Int =
    ioDeferLoop(0).unsafeRunSync()
}
