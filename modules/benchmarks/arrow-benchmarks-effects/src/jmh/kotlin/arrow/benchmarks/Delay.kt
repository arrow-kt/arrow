package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx2.Fx as Fx2
import arrow.effects.suspended.fx.effect
import arrow.effects.suspended.fx.not
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking as fx2RunBlocking
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Delay {

  @Param("50000")
  var size: Int = 0

  private tailrec suspend fun fxDirectDelayLoop(i: Int): Int =
    if (i > size) i else fxDirectDelayLoop(i + 1)

  private tailrec suspend fun fxDelayLoop(i: Int): suspend () -> Int {
    val j = !effect { i }
    return if (j > size) effect { j } else fxDelayLoop(j + 1)
  }

  private fun ioDelayLoop(i: Int): IO<Int> =
    IO { i }.flatMap { j ->
      if (j > size) IO { j } else ioDelayLoop(j + 1)
    }

  @Benchmark
  fun fxDirect(): Int =
    unsafe { fxRunBlocking { Fx { fxDirectDelayLoop(0) } } }

  @Benchmark
  fun fx2Direct(): Int =
    unsafe { fx2RunBlocking { Fx2 { fxDirectDelayLoop(0) } } }

  @Benchmark
  fun kotlinXCoroutinesDirect(): Int =
    runBlocking { fxDirectDelayLoop(0) }

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { Fx { !fxDelayLoop(0) } } }

  @Benchmark
  fun fx2(): Int =
    unsafe { fx2RunBlocking { Fx2 { !fxDelayLoop(0) } } }

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioDelayLoop(0) } }

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`Delay$`.`MODULE$`.unsafeIODelayLoop(size, 0)

  @Benchmark
  fun scalaZIO(): Int =
    arrow.benchmarks.effects.scala.zio.`Delay$`.`MODULE$`.unsafeIODelayLoop(size, 0)

}
