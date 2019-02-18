package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.typeclasses.suspended.*
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking as fxRunBlocking
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
    val j = !fx { i }
    return if (j > size) fx { j } else fxDelayLoop(j + 1)
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

  @Benchmark
  fun cats_io(): Int =
    arrow.benchmarks.effects.scala.cats.`Delay$`.`MODULE$`.unsafeIODelayLoop(size, 0)

}
