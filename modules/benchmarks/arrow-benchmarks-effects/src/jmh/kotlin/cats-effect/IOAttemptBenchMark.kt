package arrow.benchmarks.effects

import arrow.core.Either
import arrow.effects.IO
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class IOAttemptBenchMark {

  @Param("100")
  var size: Int = 0

  fun loopHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO { i + 1 }.attempt().flatMap { either ->
        when (either) {
          is Either.Left -> IO.raiseError(either.a)
          is Either.Right -> loopHappy(size, either.b)
        }
      }
    } else IO.just(1)

  fun loopNotHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO.raiseError<Int>(dummy)
        .map { it + 1 }
        .attempt()
        .flatMap { either ->
          when (either) {
            is Either.Left -> loopNotHappy(size, i + 1)
            is Either.Right -> IO.just(either.b)
          }
        }
    } else IO.just(1)

  @Benchmark
  fun happyPath(): Int =
    unsafe { runBlocking { loopHappy(size, 0) } }

  @Benchmark
  fun errorRaised(): Int =
    unsafe { runBlocking { loopNotHappy(size, 0) } }

}
