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

  @Param("100")
  var size: Int = 0

  suspend fun loopNotHappy(size: Int, i: Int): Int =
    if (i < size) {
      val attempted = !attempt {
        !dummy.raiseError<Int>()
          .map { it + 1 }
      }
      when (attempted) {
        is Either.Left -> loopNotHappy(size, i + 1)
        is Either.Right -> attempted.b
      }
    } else 1

  @Benchmark
  fun fx(): Int =
    unsafe { runBlocking { Fx { loopNotHappy(size, 0) } } }

}
