package arrow.benchmarks

import arrow.core.Either
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.*
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
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class AttemptRaisedError {

  @Param("10000")
  var size: Int = 0

  suspend fun loopNotHappy(size: Int, i: Int): Fx<Int> =
    if (i < size) {
      val attempted = !Fx {
        !Fx.raiseError<Int>(dummy).map { it + 1 }
      }.attempt()
      when (attempted) {
        is Either.Left -> loopNotHappy(size, i + 1)
        is Either.Right -> Fx.just(attempted.b)
      }
    } else Fx.just(1)

  @Benchmark
  fun fx(): Int =
    unsafe { runBlocking { Fx { loopNotHappy(size, 0)() } } }


}
