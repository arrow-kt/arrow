package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
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
open class Uncancellable {

  @Param("100")
  var size: Int = 0

  fun ioUncancelableLoop(i: Int): IO<Int> =
    if (i < size) IO { i + 1 }.uncancelable().flatMap { ioUncancelableLoop(it) }
    else IO.just(i)

  fun fxUncancelableLoop(i: Int): Fx<Int> =
    if (i < size) Fx.lazy { i + 1 }.uncancelable().flatMap { fxUncancelableLoop(it) }
    else Fx.just(i)

  @Benchmark
  fun io(): Int = ioUncancelableLoop(0).unsafeRunSync()

  @Benchmark
  fun fx(): Int = Fx.unsafeRunBlocking(fxUncancelableLoop(0))
}
