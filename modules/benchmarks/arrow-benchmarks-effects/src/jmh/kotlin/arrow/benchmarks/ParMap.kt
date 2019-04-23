package arrow.benchmarks

import arrow.data.extensions.list.foldable.foldLeft
import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.extensions.fx.concurrent.parMapN
import arrow.effects.extensions.io.concurrent.parMapN
import arrow.effects.suspended.fx.Fx
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class ParMap {

  @Param("100")
  var size: Int = 0

  private fun fxHelper(): Fx<Int> =
    (0 until size).toList().foldLeft(Fx.lazy { 0 }) { acc, i ->
      NonBlocking.parMapN(acc, Fx.lazy { i }) { a, b -> a + b }
    }

  private fun ioHelper(): IO<Int> =
    (0 until size).toList().foldLeft(IO { 0 }) { acc, i ->
      NonBlocking.parMapN(acc, IO { i }) { a, b -> a + b }
    }

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxHelper())

  @Benchmark
  fun io(): Int =
    ioHelper().unsafeRunSync()

}