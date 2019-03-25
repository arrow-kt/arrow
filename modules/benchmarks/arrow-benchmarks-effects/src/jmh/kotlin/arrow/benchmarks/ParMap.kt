package arrow.benchmarks

import arrow.data.extensions.list.foldable.foldLeft
import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.extensions.fx.concurrent.parMapN
import arrow.effects.extensions.fx2.fx.concurrent.parMapN
import arrow.effects.extensions.io.concurrent.parMapN
import arrow.effects.suspended.fx.Fx
import arrow.unsafe
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking as fx2RunBlocking
import arrow.effects.extensions.io.fx.fx as ioFx
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class ParMap {

  @Param("100")
  var size: Int = 0

  private fun fxHelper(): Fx<Int> = (0 until size).toList().foldLeft(Fx { 0 }) { acc, i ->
    NonBlocking.parMapN(acc, Fx { i }) { a, b -> a + b }
  }

  private fun fx2Helper(): arrow.effects.suspended.fx2.Fx<Int> = (0 until size).toList().foldLeft(arrow.effects.suspended.fx2.Fx { 0 }) { acc, i ->
    NonBlocking.parMapN(acc, arrow.effects.suspended.fx2.Fx { i }) { a, b -> a + b }
  }

  private fun ioHelper(): IO<Int> =
    (0 until size).toList().foldLeft(IO { 0 }) { acc, i ->
      NonBlocking.parMapN(acc, IO { i }) { a, b -> a + b }
    }

  @Benchmark
  fun fx(): Int =
    unsafe {
      fxRunBlocking {
        fxHelper()
      }
    }

  @Benchmark
  fun fx2(): Int =
    unsafe {
      fx2RunBlocking {
        fx2Helper()
      }
    }

  @Benchmark
  fun io(): Int =
    unsafe {
      ioRunBlocking {
        ioHelper()
      }
    }

}