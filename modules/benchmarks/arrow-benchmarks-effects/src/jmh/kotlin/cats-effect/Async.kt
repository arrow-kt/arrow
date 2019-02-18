package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.monad.followedBy
import arrow.effects.typeclasses.suspended.Fx
import arrow.effects.typeclasses.suspended.NonBlocking
import arrow.effects.typeclasses.suspended.continueOn
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.io.async.shift as ioShift
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.typeclasses.suspended.fx.async.shift as fxShift
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking as fxRunBlocking


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Async {

  @Param("500")
  var size: Int = 0

  tailrec suspend fun fxAsyncLoop(i: Int): suspend () -> Int =
    NonBlocking.continueOn {
      if (i > size) i else fxAsyncLoop(i + 1)()
    }

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { Fx { fxAsyncLoop(0)() } } }

  fun ioAsyncLoop(i: Int): IO<Int> =
    NonBlocking.ioShift().followedBy(
      if (i > size) IO { i } else ioAsyncLoop(i + 1)
    )

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioAsyncLoop(0) } }

  @Benchmark
  fun cats_io(): Int =
    arrow.benchmarks.effects.scala.cats.`Async$`.`MODULE$`.unsafeIOAsyncLoop(size, 0)

  @Benchmark
  fun zio(): Int =
    arrow.benchmarks.effects.scala.zio.`Async$`.`MODULE$`.unsafeIOAsyncLoop(size, 0)

}
