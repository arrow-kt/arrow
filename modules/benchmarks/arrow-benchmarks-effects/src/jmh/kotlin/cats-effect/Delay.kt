package arrow.benchmarks.effects

import arrow.core.getOrHandle
import arrow.core.right
import arrow.effects.IO
import arrow.effects.suspended.env.EnvFx
import arrow.effects.suspended.env.toFx
import arrow.effects.suspended.error.CatchFx
import arrow.effects.suspended.error.toFx
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.not
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.suspended.error.catchfx.monad.flatMap
import arrow.effects.suspended.env.envfx.monad.flatMap
import arrow.effects.suspended.fx.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Delay {

  @Param("50000")
  var size: Int = 0

  tailrec suspend fun fxDirectDelayLoop(i: Int): Int =
    if (i > size) i else fxDirectDelayLoop(i + 1)

  @Benchmark
  fun fx_direct(): Int =
    unsafe { fxRunBlocking { Fx { fxDirectDelayLoop(0) } } }

  @Benchmark
  fun kotlinx_coroutines(): Int =
    runBlocking { fxDirectDelayLoop(0) }

  tailrec suspend fun fxDelayLoop(i: Int): suspend () -> Int {
    val j = !arrow.effects.suspended.fx.fx { i }
    return if (j > size) arrow.effects.suspended.fx.fx { j } else fxDelayLoop(j + 1)
  }

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { Fx { !fxDelayLoop(0) } } }

  fun ioDelayLoop(i: Int): IO<Int> =
    IO {i }.flatMap { j ->
      if (j > size) IO { j } else ioDelayLoop(j + 1)
    }

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioDelayLoop(0) } }

  fun bioDelayLoop(i: Int): CatchFx<Int, Int> =
    CatchFx { i.right() }.flatMap { j ->
      if (j > size) CatchFx { j.right() } else bioDelayLoop(j + 1)
    }

  @Benchmark
  fun fx_rio(): Int =
    unsafe { fxRunBlocking { rioDelayLoop(0).toFx(0) }.getOrHandle { 0 } }

  fun rioDelayLoop(i: Int): EnvFx<Int, Int, Int> =
    EnvFx<Int, Int, Int> { i.right() }.flatMap { j ->
      if (j > size) EnvFx { j.right() } else rioDelayLoop(j + 1)
    }

  @Benchmark
  fun fx_bio(): Int =
    unsafe { fxRunBlocking { bioDelayLoop(0).toFx() }.getOrHandle { 0 } }

  @Benchmark
  fun cats_io(): Int =
    arrow.benchmarks.effects.scala.cats.`Delay$`.`MODULE$`.unsafeIODelayLoop(size, 0)

}
