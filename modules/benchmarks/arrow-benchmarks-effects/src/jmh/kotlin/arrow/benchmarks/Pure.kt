package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Pure {

  @Param("3000")
  var size: Int = 0

  private fun ioPureLoop(i: Int): IO<Int> =
    IO.just(i).flatMap { j ->
      if (j > size) IO.just(j) else ioPureLoop(j + 1)
    }

  fun fxPureLoop(i: Int): Fx<Int> =
    Fx.just(i).flatMap { j ->
      if (j > size) Fx.just(j) else fxPureLoop(j + 1)
    }

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxPureLoop(0))

  @Benchmark
  fun io(): Int =
    ioPureLoop(0).unsafeRunSync()

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`Pure$`.`MODULE$`.unsafeIOPureLoop(size, 0)

  @Benchmark
  fun scalazZio(): Int =
    arrow.benchmarks.effects.scala.zio.`Pure$`.`MODULE$`.unsafeIOPureLoop(size, 0)

}
