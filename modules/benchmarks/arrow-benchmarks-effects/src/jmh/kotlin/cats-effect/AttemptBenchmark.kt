package arrow.benchmarks.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.unsafeRun.unsafeRun
import arrow.effects.typeclasses.UnsafeRun
import arrow.effects.typeclasses.suspended.concurrent.Fx
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
open class AttemptBenchmark {

  @Param("10000")
  var size: Int = 0

  @Benchmark
  fun happyPath(): Int {
    fun loop(i: Int): IO<Int> =
      if (i < size) IO.just(i + 1).attempt().flatMap { it.fold({ e -> IO.raiseError<Int>(e) }, { n -> loop(n) }) }
      else IO.just(i)

    return loop(0).unsafeRunSync()
  }

  @Benchmark
  fun errorRaised(): Int {
    val dummy = RuntimeException("dummy")
    fun id(i: Int): IO<Int> = IO.just(i)

    fun loop(i: Int): IO<Int> =
      if (i < size)
        IO.raiseError<Int>(dummy)
          .flatMap { IO.just(it + 1) }
          .attempt()
          .flatMap { it.fold({ e -> loop(i + 1) }, {i -> id(i)}) }
      else
        IO.just(i)

    return loop(0).unsafeRunSync()
  }

}
