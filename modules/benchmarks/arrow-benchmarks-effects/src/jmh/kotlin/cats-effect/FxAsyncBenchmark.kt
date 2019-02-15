package arrow.benchmarks.effects

import arrow.core.Right
import arrow.data.extensions.list.foldable.foldLeft
import arrow.effects.typeclasses.suspended.*
import arrow.effects.typeclasses.suspended.fx.concurrent.parMapN
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class FxAsyncBenchmark {

  @Param("100")
  var size: Int = 0

  suspend fun evalAsync(n: Int): suspend () -> Int =
    fromAsync { _, callback -> callback(Right(n)) }

  tailrec suspend fun loopAsync(i: Int): suspend () -> Int =
    if (i < size) loopAsync(!evalAsync(i + 1))
    else evalAsync(i)

  @Benchmark
  fun async() =
    unsafe {
      runBlocking {
        Fx {
          loopAsync(0)
        }
      }
    }

  fun parMap2Task(): Fx<Int> = (0 until size).toList().foldLeft(Fx { 0 }) { acc, i ->
    NonBlocking.parMapN(acc, Fx { i }) { a, b -> a + b }
  }

  @Benchmark
  fun parMap2(): Int =
    unsafe {
      runBlocking {
        parMap2Task()
      }
    }

  tailrec suspend fun startLoop(i: Int): Int =
    if (i < size) {
      val fiber = NonBlocking.startFiber { i + 1 }
      startLoop(fiber())
    } else i

  @Benchmark
  fun start(): Int =
    unsafe {
      runBlocking {
        Fx { startLoop(0) }
      }
    }

  tailrec suspend fun bracketLoop(i: Int): Int =
    if (i < size) bracketLoop(!suspend { i }
      .bracketCase(
        { _, _ -> { unit } },
        { ib: Int -> suspend { ib + 1 } }
      ))
    else i


  @Benchmark
  fun bracketBench() =
    unsafe {
      runBlocking {
        Fx { bracketLoop(0) }
      }
    }

}