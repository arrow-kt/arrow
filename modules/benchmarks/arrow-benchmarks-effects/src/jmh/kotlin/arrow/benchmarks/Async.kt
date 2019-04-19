package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.extensions.io.monad.followedBy
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.async.shift as fxShift
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.io.async.shift as ioShift
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Async {

  @Param("3000")
  var size: Int = 0

  private fun fxAsyncLoop(i: Int): Fx<Int> =
    Fx.unit.continueOn(NonBlocking).followedBy(
      if (i > size) Fx.just(i) else fxAsyncLoop(i + 1)
    )

  private fun ioAsyncLoop(i: Int): IO<Int> =
    IO.unit.continueOn(NonBlocking).followedBy(
      if (i > size) IO.just(i) else ioAsyncLoop(i + 1)
    )

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxAsyncLoop(0))

  @Benchmark
  fun io(): Int =
    ioAsyncLoop(0).unsafeRunSync()

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`Async$`.`MODULE$`.unsafeIOAsyncLoop(size, 0)

  @Benchmark
  fun scalazZIO(): Int =
    arrow.benchmarks.effects.scala.zio.`Async$`.`MODULE$`.unsafeIOAsyncLoop(size, 0)

}
