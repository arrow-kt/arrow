package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.fx.monad.flatMap
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.flatMap
import arrow.effects.suspended.fx.just
import arrow.effects.suspended.fx.not
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking as fx2RunBlocking
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.suspended.fx2.Fx as Fx2

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Pure {

  @Param("3000")
  var size: Int = 0

  private fun fx2PureLoop(i: Int): Fx2<Int> =
    Fx2.just(i).flatMap { j ->
      if (j > size) Fx2.just(j) else fx2PureLoop(j + 1)
    }


  private fun ioPureLoop(i: Int): IO<Int> =
    IO.just(i).flatMap { j ->
      if (j > size) IO.just(j) else ioPureLoop(j + 1)
    }

  @Benchmark
  fun fx2(): Int =
    unsafe { fx2RunBlocking { fx2PureLoop(0) } }

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioPureLoop(0) } }

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`Pure$`.`MODULE$`.unsafeIOPureLoop(size, 0)

  @Benchmark
  fun scalazZio(): Int =
    arrow.benchmarks.effects.scala.zio.`Pure$`.`MODULE$`.unsafeIOPureLoop(size, 0)

}
