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
open class Delay {

  @Param("3000")
  var size: Int = 0

  private fun fxDelayLoop(i: Int): Fx<Int> =
    Fx { i }.flatMap { j ->
      if (j > size) Fx { j } else fxDelayLoop(j + 1)
    }

  private fun ioDelayLoop(i: Int): IO<Int> =
    IO { i }.flatMap { j ->
      if (j > size) IO { j } else ioDelayLoop(j + 1)
    }

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxDelayLoop(0))

  @Benchmark
  fun io(): Int =
    ioDelayLoop(0).unsafeRunSync()

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`Delay$`.`MODULE$`.unsafeIODelayLoop(size, 0)

  @Benchmark
  fun scalaZIO(): Int =
    arrow.benchmarks.effects.scala.zio.`Delay$`.`MODULE$`.unsafeIODelayLoop(size, 0)

}
