package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking

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

  fun fxDeferLoop(i: Int): Fx<Int> =
    Fx.defer { Fx.just(i) }.flatMap { j ->
      if (j > size) Fx.defer { Fx.just(j) } else fxDeferLoop(j + 1)
    }

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxDeferLoop(0))

  @Benchmark
  fun io(): Int =
    ioDeferLoop(0).unsafeRunSync()

}
