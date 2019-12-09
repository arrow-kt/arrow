package arrow.benchmarks

import arrow.core.Right
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
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
open class Cancellable {

  @Param("100")
  var size: Int = 0

  fun evalCancelable(n: Int): IO<Int> =
    IO.concurrent().cancelable<Int> { cb ->
      cb(Right(n))
      IO.unit
    }.fix()

  fun cancelableLoop(i: Int): IO<Int> =
    if (i < size) evalCancelable(i + 1).flatMap { cancelableLoop(it) }
    else evalCancelable(i)

  @Benchmark
  fun io(): Int =
    cancelableLoop(0).unsafeRunSync()
}
