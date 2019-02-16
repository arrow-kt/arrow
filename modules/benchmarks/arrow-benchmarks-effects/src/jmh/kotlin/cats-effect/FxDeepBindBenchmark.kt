package arrow.benchmarks.effects

import arrow.effects.typeclasses.suspended.*
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class FxDeepBindBenchmark {

  @Param("50000")
  var size: Int = 0

  tailrec suspend fun pureLoop(i: Int): suspend () -> Int {
    val j = !just(i)
    return if (j > size) just(j) else pureLoop(j + 1)
  }

  @Benchmark
  fun pure(): Int =
    unsafe { runBlocking { Fx { !pureLoop(0) } } }

  tailrec suspend fun delayLoop(i: Int): suspend () -> Int {
    val j = !suspend { i }
    return if (j > size) suspend { j } else delayLoop(j + 1)
  }

  @Benchmark
  fun delay(): Int =
    unsafe { runBlocking { Fx { !delayLoop(0) } } }

  suspend fun asyncLoop(i: Int): suspend () -> Int = {
    val j = !suspend { i }
    !NonBlocking.startFiber {
      !if (j > size) suspend { j } else asyncLoop(j + 1)
    }
  }

  @Benchmark
  fun async(): Int =
    unsafe { runBlocking { Fx { !asyncLoop(0) } } }

}
