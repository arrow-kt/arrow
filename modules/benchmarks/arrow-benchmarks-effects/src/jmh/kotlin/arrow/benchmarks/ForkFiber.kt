package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.fix
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.concurrent.startFiber as fxStartFiber
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.io.concurrent.startFiber as ioStartFiber
import arrow.effects.extensions.io.fx.fx as ioFx
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class ForkFiber {

  @Param("100")
  var size: Int = 0

  private fun fxStartLoop(i: Int): Fx<Int> =
    if (i < size) {
      NonBlocking.fxStartFiber(Fx { i + 1 }).flatMap {
        it.join().fix().flatMap(::fxStartLoop)
      }
    } else Fx.just(i)

  private fun ioStartLoop(i: Int): IO<Int> =
    if (i < size) {
      NonBlocking.ioStartFiber(IO { i + 1 }).flatMap {
        it.join().flatMap(::ioStartLoop)
      }
    } else IO.just(i)


  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioStartLoop(0) } }

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { fxStartLoop(0) } }

}