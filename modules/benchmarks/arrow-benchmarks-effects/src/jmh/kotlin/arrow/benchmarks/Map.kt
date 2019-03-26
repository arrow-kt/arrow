package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.just as fxJust
import arrow.effects.suspended.fx.map as fxMap
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking as fx2RunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Map {

  @Benchmark
  fun ioOne(): Long = ioTest(12000, 1)

  @Benchmark
  fun ioBatch30(): Long = ioTest(12000 / 30, 30)

  @Benchmark
  fun ioBatch120(): Long = ioTest(12000 / 120, 120)

  @Benchmark
  fun fxOne(): Long =
    unsafe { runBlocking { fxTest(12000, 1) } }

  @Benchmark
  fun fxBatch30(): Long =
    unsafe { runBlocking { fxTest(12000 / 30, 30) } }

  @Benchmark
  fun fxBatch120(): Long =
    unsafe { runBlocking { fxTest(12000 / 120, 120) } }

  @Benchmark
  fun fx2One(): Long =
    unsafe { fx2RunBlocking { arrow.effects.suspended.fx2.Fx { fx2Test(12000, 1) } } }

  @Benchmark
  fun fx2Batch30(): Long =
    unsafe { fx2RunBlocking { arrow.effects.suspended.fx2.Fx { fx2Test(12000 / 30, 30) } } }

  @Benchmark
  fun fx2Batch120(): Long =
    unsafe { fx2RunBlocking { arrow.effects.suspended.fx2.Fx { fx2Test(12000 / 120, 120) } } }

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
    var fx = fxJust(0)

    var j = 0
    while (j < batch) {
      fx = fx.fxMap(f); j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += fx()
      i += 1
    }
    return sum
  }

  private suspend fun fx2Test(iterations: Int, batch: Int): Long {
    val f = { x: Int -> x + 1 }
    var fx = arrow.effects.suspended.fx2.Fx.just(0)

    var j = 0
    while (j < batch) {
      fx = fx.map(f); j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += !fx
      i += 1
    }
    return sum
  }

}