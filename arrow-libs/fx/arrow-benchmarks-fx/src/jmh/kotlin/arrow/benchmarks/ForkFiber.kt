package arrow.benchmarks

import arrow.fx.IO
import arrow.fx.IODispatchers
import arrow.fx.fix
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
open class ForkFiber {

  @Param("100")
  var size: Int = 0

  private fun ioStartLoop(i: Int): IO<Int> =
    if (i < size) {
      IO { i + 1 }.fork(IODispatchers.CommonPool).flatMap { fiber ->
        fiber.join().fix().flatMap { ioStartLoop(it) }
      }
    } else IO.just(i)

  @Benchmark
  fun io(): Int =
    ioStartLoop(0).unsafeRunSync()
}
