package arrow.benchmarks.effects

import arrow.effects.typeclasses.suspended.Fx
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

val dummy = object : RuntimeException("dummy") {
  override fun fillInStackTrace(): Throwable =
    this
}

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
open class FxAttemptBenchMark {

  @Param("10000")
  var size: Int = 0

  fun loopHappy(size: Int, i: Int): Fx<Int> =
    if (i < size) {
      Fx { i + 1 }.attempt().flatMap { either ->
        either.fold({ Fx.raiseError<Int>(it) }, { n -> loopHappy(size, n) })
      }
    } else Fx.just(1)

  fun loopNotHappy(size: Int, i: Int): Fx<Int> =
    if (i < size) {
      Fx.raiseError<Int>(dummy)
        .map { it + 1 }
        .attempt()
        .flatMap { either ->
          either.fold({ loopNotHappy(size, i + 1) }, { Fx.just(it) })
        }
    } else Fx.just(1)

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  @Benchmark
  fun happyPath(): Int =
    unsafe { runBlocking { loopHappy(size, 0) } }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  @Benchmark
  fun errorRaised(): Int =
    unsafe { runBlocking { loopNotHappy(size, 0) } }

}
