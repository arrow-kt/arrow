package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.typeclasses.suspended.just
import arrow.effects.typeclasses.suspended.map
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Map {

  @Benchmark
  fun io_one(): Long = ioTest(12000, 1)

  @Benchmark
  fun io_batch30(): Long = ioTest(12000 / 30, 30)

  @Benchmark
  fun io_batch120(): Long = ioTest(12000 / 120, 120)

  @Benchmark
  fun fx_one(): Long =
    unsafe { runBlocking { fxTest(12000, 1) } }

  @Benchmark
  fun fx_batch30(): Long =
    unsafe { runBlocking { fxTest(12000 / 30, 30) } }

  @Benchmark
  fun fx_batch120(): Long =
    unsafe { runBlocking { fxTest(12000 / 120, 120) } }

  @Benchmark
  fun kotlinx_coroutines_one(): Long =
    runBlocking { fxTest(12000, 1) }

  @Benchmark
  fun kotlinx_coroutines_batch30(): Long =
    runBlocking { fxTest(12000 / 30, 30) }

  @Benchmark
  fun kotlinx_coroutines_batch120(): Long =
    runBlocking { fxTest(12000 / 120, 120) }

  @Benchmark
  fun fx_direct_one(): Long =
    unsafe { runBlocking { fxTestDirect(12000, 1) } }

  @Benchmark
  fun fx_direct_batch30(): Long =
    unsafe { runBlocking { fxTestDirect(12000 / 30, 30) } }

  @Benchmark
  fun fx_direct_batch120(): Long =
    unsafe { runBlocking { fxTestDirect(12000 / 120, 120) } }

  @Benchmark
  fun kotlinx_direct_coroutines_one(): Long =
    runBlocking { fxTestDirect(12000, 1) }

  @Benchmark
  fun kotlinx_direct_coroutines_batch30(): Long =
    runBlocking { fxTestDirect(12000 / 30, 30) }

  @Benchmark
  fun kotlinx_direct_coroutines_batch120(): Long =
    runBlocking { fxTestDirect(12000 / 120, 120) }

  private fun ioTest(iterations: Int, batch: Int): Long {
    val f = {x: Int -> x + 1}
    var io = IO{0}

    var j = 0
    while (j < batch) { io = io.map(f); j += 1 }

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
    var fx = just(0)

    var j = 0
    while (j < batch) { fx = fx.map(f); j += 1 }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += fx()
      i += 1
    }
    return sum
  }

  private suspend fun fxTestDirect(iterations: Int, batch: Int): Long {
    var fx = 0
    var j = 0
    while (j < batch) { fx += 1; j += 1 }
    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += fx
      i += 1
    }
    return sum
  }

}