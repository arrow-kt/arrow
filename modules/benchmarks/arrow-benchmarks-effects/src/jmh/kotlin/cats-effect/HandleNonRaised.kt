package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.handleErrorWith
import arrow.effects.typeclasses.suspended.*
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class HandleNonRaised {

  @Param("10000")
  var size: Int = 0

  fun ioHappyPathLoop(i: Int): IO<Int> = if (i < size)
    IO.just(i + 1)
      .handleErrorWith { IO.raiseError(it) }
      .flatMap { ioHappyPathLoop(it) }
  else
    IO.just(i)

  @Benchmark
  fun io(): Int =
    ioHappyPathLoop(0).unsafeRunSync()

  tailrec suspend fun fxHappyPathLoop(i: Int): Int =
    if (i < size) {
      val n = !just(i + 1).handleErrorWith { raiseError(it) }
      fxHappyPathLoop(n)
    } else i

  @Benchmark
  fun fx(): Int =
    unsafe { runBlocking { Fx { fxHappyPathLoop(0) } } }

  tailrec suspend fun fxDirectHappyPathLoop(i: Int): Int =
    if (i < size) {
      val n = try {
        i + 1
      } catch (t : Throwable) {
        throw t
      }
      fxDirectHappyPathLoop(n)
    } else i

  @Benchmark
  fun fx_direct(): Int =
    unsafe { runBlocking { Fx { fxDirectHappyPathLoop(0) } } }

}