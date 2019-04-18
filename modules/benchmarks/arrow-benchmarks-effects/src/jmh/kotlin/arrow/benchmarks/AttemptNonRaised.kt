package arrow.benchmarks

import arrow.benchmarks.effects.scala.zio.ZIORTS
import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class AttemptNonRaised {

  @Param("10000")
  var size: Int = 0

  private fun fxLoopHappy(size: Int, i: Int): Fx<Int> =
    if (i < size) {
      Fx { i + 1 }.attempt().flatMap {
        it.fold(Fx.Companion::raiseError) { n -> fxLoopHappy(size, n) }
      }
    } else Fx.just(1)

  private fun ioLoopHappy(size: Int, i: Int): IO<Int> =
    if (i < size) {
      IO { i + 1 }.attempt().flatMap {
        it.fold(IO.Companion::raiseError) { n -> ioLoopHappy(size, n) }
      }
    } else IO.just(1)

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxLoopHappy(size, 0))

  @Benchmark
  fun io(): Int =
    ioLoopHappy(size, 0).unsafeRunSync()

  @Benchmark
  fun cats(): Any =
    arrow.benchmarks.effects.scala.cats.AttemptNonRaised.ioLoopHappy(size, 0).unsafeRunSync()

  @Benchmark
  fun zio(): Any =
    arrow.benchmarks.effects.scala.zio.AttemptNonRaised.run(size)

}
