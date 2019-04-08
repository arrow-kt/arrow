package arrow.benchmarks

import arrow.benchmarks.effects.scala.zio.ZIORTS
import arrow.core.Either
import arrow.effects.IO
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.*
import arrow.unsafe
import org.openjdk.jmh.annotations.*
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
      Fx { throw dummy }.attempt().flatMap {
        it.fold({ fxLoopNotHappy(size, i + 1) }) { n -> Fx.just(n) }
      }
    } else Fx.just(1)

  private fun ioLoopNotHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO { throw dummy }.attempt().flatMap {
        it.fold({ ioLoopNotHappy(size, i + 1) }) { n -> IO.just(n) }
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
    ZIORTS.unsafeRun(
      arrow.benchmarks.effects.scala.zio.AttemptRaisedError.ioLoopNotHappy(size, 0)
    )

}
