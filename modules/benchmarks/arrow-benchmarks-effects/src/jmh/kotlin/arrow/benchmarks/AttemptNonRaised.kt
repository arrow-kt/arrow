package arrow.benchmarks

import arrow.core.Either
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class AttemptNonRaised {

  @Param("10000")
  var size: Int = 0

  tailrec suspend fun loopHappy(size: Int, i: Int): Fx<Int> =
    if (i < size) {
      val attempted = !Fx.att { i + 1 }
      when (attempted) {
        is Either.Left -> !attempted.a.raiseError<Int>()
        is Either.Right -> loopHappy(size, attempted.b)
      }
    } else Fx.just(1)

  @Benchmark
  fun fx(): Int =
    unsafe { runBlocking { Fx { loopHappy(size, 0) } } }

  @Benchmark
  fun fx2(): Int =
    unsafe { fx2RunBlocking { arrow.effects.suspended.fx2.Fx { loopHappy(size, 0) } } }

}
