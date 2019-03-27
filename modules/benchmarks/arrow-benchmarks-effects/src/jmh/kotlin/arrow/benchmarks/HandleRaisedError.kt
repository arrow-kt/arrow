package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.handleErrorWith
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class HandleRaisedError {

  @Param("10000")
  var size: Int = 0

  private val dummy = RuntimeException("dummy")

  private fun ioErrorRaisedloop(i: Int): IO<Int> =
    if (i < size)
      IO.raiseError<Int>(dummy)
        .flatMap { x -> IO.just(x + 1) }
        .flatMap { x -> IO.just(x + 1) }
        .handleErrorWith { ioErrorRaisedloop(i + 1) }
    else
      IO.just(i)

  private fun fxErrorRaisedloop(i: Int): Fx<Int> =
    if (i < size)
      Fx.raiseError<Int>(dummy)
        .flatMap { x -> Fx.just(x + 1) }
        .flatMap { x -> Fx.just(x + 1) }
        .handleErrorWith { fxErrorRaisedloop(i + 1) }
    else
      Fx.just(i)


  @Benchmark
  fun io(): Int =
    ioErrorRaisedloop(0).unsafeRunSync()

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { fxErrorRaisedloop(0) } }

}