package arrow.benchmarks

import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

val dummy = object : RuntimeException("dummy") {
  override fun fillInStackTrace(): Throwable =
    this
}

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class AttemptRaisedError {

  @Param("10000")
  var size: Int = 0

  private fun fxLoopNotHappy(size: Int, i: Int): Fx<Int> =
    if (i < size) {
      Fx.lazy { throw dummy }.attempt().flatMap {
        it.fold({ fxLoopNotHappy(size, i + 1) }, Fx.Companion::just)
      }
    } else Fx.just(1)

  private fun ioLoopNotHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO { throw dummy }.attempt().flatMap {
        it.fold({ ioLoopNotHappy(size, i + 1) }, IO.Companion::just)
      }
    } else IO.just(1)

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxLoopNotHappy(size, 0))

  @Benchmark
  fun io(): Int =
    ioLoopNotHappy(size, 0).unsafeRunSync()

  @Benchmark
  fun cats(): Any =
    arrow.benchmarks.effects.scala.cats.AttemptRaisedError.ioLoopNotHappy(size, 0).unsafeRunSync()

  @Benchmark
  fun zio(): Any =
    arrow.benchmarks.effects.scala.zio.AttemptRaisedError.run(size)
}
