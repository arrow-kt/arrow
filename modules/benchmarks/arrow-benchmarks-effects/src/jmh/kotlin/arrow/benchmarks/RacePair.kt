package arrow.benchmarks

import arrow.core.Either
import arrow.data.extensions.list.foldable.foldLeft
import arrow.effects.IO
import arrow.effects.extensions.NonBlocking
import arrow.effects.extensions.io.applicative.map
import arrow.effects.extensions.fx.applicative.map
import arrow.effects.racePair
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.racePair
import org.openjdk.jmh.annotations.*
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
    IO.racePair(NonBlocking, acc, IO { 1 }).flatMap { ei ->
      when (ei) {
        is Either.Left -> ei.a.b.cancel().map { ei.a.a }
        is Either.Right -> ei.b.a.cancel().map { ei.b.b }
      }
    }
  }

  private fun fxRacePairHelper(): Fx<Int> = (0 until size).toList().foldLeft(Fx.lazy { 0 }) { acc, _ ->
    Fx.racePair(NonBlocking, acc, Fx.lazy { 1 }).flatMap { ei ->
      when (ei) {
        is Either.Left -> ei.a.b.cancel().map { ei.a.a }
        is Either.Right -> ei.b.a.cancel().map { ei.b.b }
      }
    }
  }

  @Benchmark
  fun io(): Int = racePairHelper().unsafeRunSync()

  @Benchmark
  fun fx(): Int = Fx.unsafeRunBlocking(fxRacePairHelper())

}