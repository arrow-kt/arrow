package arrow.benchmarks

import arrow.core.Right
import arrow.effects.IO
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.fix
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Cancellable {

  @Param("100")
  var size: Int = 0

  fun evalCancelable(n: Int): IO<Int> =
    IO.concurrent().cancelable<Int> { cb ->
      cb(Right(n))
      IO.unit
    }.fix()

  fun cancelableLoop(i: Int): IO<Int> =
    if (i < size) evalCancelable(i + 1).flatMap { cancelableLoop(it) }
    else evalCancelable(i)

  @Benchmark
  fun io(): Int =
    cancelableLoop(0).unsafeRunSync()

}