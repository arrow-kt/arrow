package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.invoke
import arrow.effects.suspended.fx.not
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.io.fx.fx as ioFx
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.extensions.startFiber as fxStartFiber
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking as fx2RunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class ForkFiber {

  @Param("100")
  var size: Int = 0

  tailrec suspend fun startLoop(i: Int): Int =
    if (i < size) {
      val fiber = NonBlocking.fxStartFiber { i + 1 }
      startLoop(!fiber)
    } else i

  private fun ioStartLoop(i: Int): IO<Int> = ioFx {
    if (i < size) {
      val fiber = !NonBlocking.startFiber(effect { i + 1 })
      !ioStartLoop(!fiber.join())
    } else i
  }

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioStartLoop(0) } }

  @Benchmark
  fun fx(): Int =
    unsafe {
      fxRunBlocking {
        Fx { startLoop(0) }
      }
    }

  @Benchmark
  fun fx2(): Int =
    unsafe {
      fx2RunBlocking {
        arrow.effects.suspended.fx2.Fx {
          startLoop(0)
        }
      }
    }

  @Benchmark
  fun kotlinXCoroutines(): Int =
    runBlocking {
      Fx { startLoop(0) }()
    }


}