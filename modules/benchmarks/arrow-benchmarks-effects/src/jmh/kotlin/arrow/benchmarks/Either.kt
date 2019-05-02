package arrow.benchmarks

import arrow.core.Either
import arrow.core.identity
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 4, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Either {

  val rInt = Either.right(1)

  @Benchmark
  fun eithFold(): Int = rInt.fold({ throw IllegalStateException("Impossible") }, ::identity)

  @Benchmark
  fun eithWhen(): Int =
    when (rInt) {
      is Either.Left -> throw IllegalStateException("Impossible")
      is Either.Right -> rInt.b
    }

}
