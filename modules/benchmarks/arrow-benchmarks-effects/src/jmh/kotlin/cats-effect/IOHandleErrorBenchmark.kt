package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.handleErrorWith
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class IOHandleErrorBenchmark {

  @Param("10000")
  var size: Int = 0

  fun happyPathloop(i: Int): IO<Int> = if (i < size)
    IO.just(i + 1)
      .handleErrorWith { IO.raiseError(it) }
      .flatMap { happyPathloop(it) }
  else
    IO.just(i)

  @Benchmark
  fun happyPath(): Int =
    happyPathloop(0).unsafeRunSync()

  val dummy = RuntimeException("dummy")

  fun errorRaisedloop(i: Int): IO<Int> =
    if (i < size)
      IO.raiseError<Int>(dummy)
        .flatMap { x -> IO.just(x + 1) }
        .flatMap { x -> IO.just(x + 1) }
        .handleErrorWith { errorRaisedloop(i + 1) }
    else
      IO.just(i)

  @Benchmark
  fun errorRaised(): Int =
    errorRaisedloop(0).unsafeRunSync()

}