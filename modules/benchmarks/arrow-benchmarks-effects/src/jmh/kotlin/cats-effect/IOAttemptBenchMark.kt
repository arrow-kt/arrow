package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit


@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open class IOAttemptBenchMark {

  @Param("1000")
  var size: Int = 0

  fun loopHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO { i + 1 }.attempt().flatMap { either ->
        either.fold({ IO.raiseError<Int>(it) }, { n -> loopHappy(size, n) })
      }
    } else IO.just(1)

  fun loopNotHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO.raiseError<Int>(dummy)
        .map { it + 1 }
        .attempt()
        .flatMap { either ->
          either.fold({ loopNotHappy(size, i + 1) }, { IO.just(it) })
        }
    } else IO.just(1)

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  @Benchmark
  fun happyPath(): Int =
    unsafe { runBlocking { loopHappy(size, 0) } }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  @Benchmark
  fun errorRaised(): Int =
    unsafe { runBlocking { loopNotHappy(size, 0) } }

}
