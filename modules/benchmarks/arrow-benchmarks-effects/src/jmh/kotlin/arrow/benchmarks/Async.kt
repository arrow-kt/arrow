package arrow.benchmarks

import arrow.core.getOrHandle
import arrow.core.right
import arrow.effects.IO
import arrow.effects.extensions.io.monad.followedBy
import arrow.effects.suspended.env.EnvFx
import arrow.effects.suspended.env.envfx.monad.followedBy
import arrow.effects.suspended.env.toFx
import arrow.effects.suspended.error.CatchFx
import arrow.effects.suspended.error.catchfx.monad.followedBy
import arrow.effects.suspended.error.toFx
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.NonBlocking
import arrow.effects.suspended.fx.continueOn
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.io.async.shift as ioShift
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.suspended.env.envfx.async.shift as rioShift
import arrow.effects.suspended.error.catchfx.async.shift as bioShift
import arrow.effects.suspended.fx.fx.async.shift as fxShift
import arrow.effects.suspended.fx.fx.unsafeRun.runBlocking as fxRunBlocking


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Async {

  @Param("50000")
  var size: Int = 0

  private suspend fun fxAsyncLoop(i: Int): suspend () -> Int =
    NonBlocking.continueOn {
      if (i > size) i else fxAsyncLoop(i + 1)()
    }

  private fun ioAsyncLoop(i: Int): IO<Int> =
    NonBlocking.ioShift().followedBy(
      if (i > size) IO { i } else ioAsyncLoop(i + 1)
    )

  private fun catchFxAsyncLoop(i: Int): CatchFx<Int, Int> =
    NonBlocking.bioShift<Int>().followedBy(
      if (i > size) CatchFx { i.right() } else catchFxAsyncLoop(i + 1)
    )

  private fun rioAsyncLoop(i: Int): EnvFx<Int, Int, Int> =
    NonBlocking.rioShift<Int, Int>().followedBy(
      if (i > size) EnvFx { i.right() } else rioAsyncLoop(i + 1)
    )

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { Fx { fxAsyncLoop(0)() } } }

  @Benchmark
  fun catchFx(): Int =
    unsafe { fxRunBlocking { catchFxAsyncLoop(0).toFx() }.getOrHandle { 0 } }

  @Benchmark
  fun envFx(): Int =
    unsafe { fxRunBlocking { rioAsyncLoop(0).toFx(0) }.getOrHandle { 0 } }

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioAsyncLoop(0) } }

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`Async$`.`MODULE$`.unsafeIOAsyncLoop(size, 0)

  @Benchmark
  fun scalazZIO(): Int =
    arrow.benchmarks.effects.scala.zio.`Async$`.`MODULE$`.unsafeIOAsyncLoop(size, 0)

}
