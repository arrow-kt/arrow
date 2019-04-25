package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.handleErrorWith as ioHandleErrorWith
import arrow.effects.suspended.fx.handleErrorWith as fxHandleErrorWith

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class HandleNonRaised {

  @Param("10000")
  var size: Int = 0

  private fun ioHappyPathLoop(i: Int): IO<Int> =
    if (i < size)
      IO.just(i + 1)
        .ioHandleErrorWith { IO.raiseError(it) }
        .flatMap { ioHappyPathLoop(it) }
    else
      IO.just(i)

  private fun fxHappyPathLoop(i: Int): Fx<Int> =
    if (i < size)
      Fx.just(i + 1)
        .fxHandleErrorWith { Fx.raiseError(it) }
        .flatMap { fxHappyPathLoop(it) }
    else
      Fx.just(i)

  @Benchmark
  fun io(): Int =
    ioHappyPathLoop(0).unsafeRunSync()

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { fxHappyPathLoop(0) } }
}
