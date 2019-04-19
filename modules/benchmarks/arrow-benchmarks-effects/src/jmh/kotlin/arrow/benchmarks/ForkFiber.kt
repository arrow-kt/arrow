package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.fix
import arrow.effects.fork
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.fix
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
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
      Fx { i + 1 }.fork(NonBlocking).flatMap {
        it.join().fix().flatMap(::fxStartLoop)
      }
    } else Fx.just(i)

  private fun ioStartLoop(i: Int): IO<Int> =
    if (i < size) {
      IO { i + 1 }.fork(NonBlocking).flatMap {
        it.join().fix().flatMap(::ioStartLoop)
      }
    } else IO.just(i)

  @Benchmark
  fun io(): Int =
    ioStartLoop(0).unsafeRunSync()

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxStartLoop(0))

}