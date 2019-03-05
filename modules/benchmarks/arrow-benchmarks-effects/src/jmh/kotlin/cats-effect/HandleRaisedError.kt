package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.handleErrorWith
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.flatMap
import arrow.effects.suspended.fx.handleError
import arrow.effects.suspended.fx.not
import arrow.effects.suspended.fx.fx.unsafeRun.runBlocking
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class HandleRaisedError {

  @Param("10000")
  var size: Int = 0

  val dummy = RuntimeException("dummy")

  fun ioErrorRaisedloop(i: Int): IO<Int> =
    if (i < size)
      IO.raiseError<Int>(dummy)
        .flatMap { x -> IO.just(x + 1) }
        .flatMap { x -> IO.just(x + 1) }
        .handleErrorWith { ioErrorRaisedloop(i + 1) }
    else
      IO.just(i)

  @Benchmark
  fun io(): Int =
    ioErrorRaisedloop(0).unsafeRunSync()

  tailrec suspend fun fxErrorRaisedloop(i: Int): Int =
    if (i < size) {
      val result = !arrow.effects.suspended.fx.raiseError<Int>(dummy)
        .flatMap { x -> arrow.effects.suspended.fx.just(x + 1) }
        .flatMap { x -> arrow.effects.suspended.fx.just(x + 1) }
        .handleError { i + 1 }
      fxErrorRaisedloop(result)
    } else i

  @Benchmark
  fun fx(): Int =
    unsafe { runBlocking { Fx { fxErrorRaisedloop(0) } } }

  tailrec suspend fun fxDirectErrorRaisedloop(i: Int): Int =
    if (i < size) {
      val result = try {
        val n: Int = throw dummy
        n + 1 + 1
      } catch (t: Throwable) {
        i + 1
      }
      fxDirectErrorRaisedloop(result)
    } else i

  @Benchmark
  fun fx_direct(): Int =
    unsafe { runBlocking { Fx { fxDirectErrorRaisedloop(0) } } }

  @Benchmark
  fun kotlinx_coroutines(): Int =
    runBlocking { fxDirectErrorRaisedloop(0) }

}