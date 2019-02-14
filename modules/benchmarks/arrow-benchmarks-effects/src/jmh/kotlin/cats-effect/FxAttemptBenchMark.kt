package arrow.benchmarks.effects

import arrow.core.Either
import arrow.effects.typeclasses.suspended.*
import kotlinx.coroutines.runBlocking
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
open class FxAttemptBenchMark {

  @Param("100000")
  var size: Int = 0

  tailrec suspend fun loopHappy(size: Int, i: Int): Int =
    if (i < size) {
      val attempted = !attempt { i + 1 }
      when (attempted) {
        is Either.Left -> !attempted.a.raiseError<Int>()
        is Either.Right -> loopHappy(size, attempted.b)
      }
    } else 1


  suspend fun loopNotHappy(size: Int, i: Int): Int =
    if (i < size) {
      val attempted = !dummy.raiseError<Int>()
        .map { it + 1 }
        .attempt()
      when (attempted) {
        is Either.Left -> loopNotHappy(size, i + 1)
        is Either.Right -> attempted.b
      }
    } else 1



  @Benchmark
  fun happyPath(): Int =
    runBlocking { !fx { loopHappy(size, 0) } }

  @Benchmark
  fun errorRaised(): Int =
    runBlocking { !fx { loopNotHappy(size, 0) } }

}
