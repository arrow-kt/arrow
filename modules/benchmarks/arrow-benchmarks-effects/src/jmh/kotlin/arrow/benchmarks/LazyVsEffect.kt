package arrow.benchmarks

import arrow.effects.IO
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
open class LazyVsEffect {

  @Param("3000")
  var size: Int = 0

  private fun ioLazyLoop(i: Int): IO<Int> =
    IO.lazy { i }.flatMap { j ->
      if (j > size) IO.lazy { j } else ioLazyLoop(j + 1)
    }

  private fun ioEffectLoop(i: Int): IO<Int> =
    IO.effect { i }.flatMap { j ->
      if (j > size) IO.effect { j } else ioLazyLoop(j + 1)
    }

  fun ioLazy(): Int =
    ioLazyLoop(0).unsafeRunSync()

  fun ioEffect(): Int=
    ioEffectLoop(0).unsafeRunSync()
}
