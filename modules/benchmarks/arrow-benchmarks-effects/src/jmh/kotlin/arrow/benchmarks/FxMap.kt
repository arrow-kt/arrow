package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class FxMap {

  @Benchmark
  fun ioOne(): Long = ioTest(12000, 1)

  @Benchmark
  fun fxOne(): Long =
    unsafe { runBlocking { fxTest(12000, 1) } }

  private fun ioTest(iterations: Int, batch: Int): Long {
    val f = { x: Int -> x + 1 }
    var io = IO.just(0)

    var j = 0
    while (j < batch) {
      io = io.map(f); j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += io.unsafeRunSync()
      i += 1
    }
    return sum
  }

  private suspend fun fxTest(iterations: Int, batch: Int): Long {
    val f = { x: Int -> x + 1 }
    var fx = Fx.just(0)

    var j = 0
    while (j < batch) {
      fx = fx.map(f); j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += fx()
      i += 1
    }
    return sum
  }

}