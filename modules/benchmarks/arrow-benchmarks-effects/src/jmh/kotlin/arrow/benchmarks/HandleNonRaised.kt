package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.handleErrorWith
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.handleErrorWith
import arrow.effects.suspended.fx.invoke
import arrow.effects.suspended.fx.not
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class HandleNonRaised {

  @Param("10000")
  var size: Int = 0

  private fun ioHappyPathLoop(i: Int): IO<Int> = if (i < size)
    IO.just(i + 1)
      .handleErrorWith { IO.raiseError(it) }
      .flatMap { ioHappyPathLoop(it) }
  else
    IO.just(i)

  private tailrec suspend fun fxHappyPathLoop(i: Int): Int =
    if (i < size) {
      val n = !arrow.effects.suspended.fx.just(i + 1).handleErrorWith { arrow.effects.suspended.fx.raiseError(it) }
      fxHappyPathLoop(n)
    } else i

  private tailrec suspend fun fxDirectHappyPathLoop(i: Int): Int =
    if (i < size) {
      val n = try {
        i + 1
      } catch (t: Throwable) {
        throw t
      }
      fxDirectHappyPathLoop(n)
    } else i

  @Benchmark
  fun io(): Int =
    ioHappyPathLoop(0).unsafeRunSync()

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { Fx { fxHappyPathLoop(0) } } }

  @Benchmark
  fun fxDirect(): Int =
    unsafe { fxRunBlocking { Fx { fxDirectHappyPathLoop(0) } } }

  @Benchmark
  fun kotlinXCoroutines(): Int =
    runBlocking { fxDirectHappyPathLoop(0) }

}