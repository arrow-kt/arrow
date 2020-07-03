package arrow.benchmarks

import arrow.core.extensions.list.foldable.foldLeft
import arrow.fx.IO
import arrow.fx.IODispatchers
import arrow.fx.extensions.io.monad.map
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class RacePair {

  @Param("100")
  var size: Int = 0

  private fun racePairHelper(): IO<Int> = (0 until size).toList().foldLeft(IO { 0 }) { acc, _ ->
    IO.racePair(IODispatchers.CommonPool, acc, IO { 1 }).flatMap { ei ->
      ei.fold({ a, (_, cancel) ->
        cancel.map { a }
      }, { (_, cancel), b ->
        cancel.map { b }
      })
    }
  }

  @Benchmark
  fun io(): Int = racePairHelper().unsafeRunSync()
}
