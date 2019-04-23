package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.fx.bracket.bracket
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Bracket {

  @Param("100")
  var size: Int = 0

  private fun fxBracketLoop(i: Int): Fx<Int> =
    if (i < size)
      Fx.just(i).bracket({ Fx.unit }, { ib -> Fx.lazy { ib + 1 } }).flatMap { fxBracketLoop(it) }
    else
      Fx.just(i)

  private fun ioBracketLoop(i: Int): IO<Int> =
    if (i < size)
      IO.just(i).bracket({ IO.unit }, { ib -> IO { ib + 1 } }).flatMap { ioBracketLoop(it) }
    else
      IO.just(i)


  @Benchmark
  fun fx() =
    unsafe {
      runBlocking {
        fxBracketLoop(0)
      }
    }

  @Benchmark
  fun io() =
    unsafe {
      ioRunBlocking {
        ioBracketLoop(0)
      }
    }

}