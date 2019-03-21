package arrow.benchmarks

import arrow.effects.IO
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Uncancellable {

  @Param("100")
  var size: Int = 0

  fun uncancelableLoop(i: Int): IO<Int> =
    if (i < size)
      IO { i + 1 }.uncancelable().flatMap { uncancelableLoop(it) }
    else
      IO.just(i)

  @Benchmark
  fun io(): Int =
    uncancelableLoop(0).unsafeRunSync()


}