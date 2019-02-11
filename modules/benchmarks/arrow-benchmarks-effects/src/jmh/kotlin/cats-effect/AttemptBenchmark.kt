package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
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
      fx {
        if (i < size) {
          !attempt { i + 1 }.flatMap { it.fold({ e -> e.raiseError<Int>() }, { n -> loop(n) }) }
        }
        else !suspend { 1 }.effect()
      }

    return loop(0).unsafeRunSync()
  }

  @Benchmark
  fun errorRaised(): Int {
    val dummy = RuntimeException("dummy")

    suspend fun id(i: Int) = i

    fun loop(i: Int): IO<Int> =
      fx {
        if (i < size)
          !dummy.raiseError<Int>()
                  .flatMap { suspend { it + 1 }.effect() }
                  .attempt()
                  .flatMap { it.fold({loop(i + 1)},{ i -> effect {id(i)}})}
        else
          !suspend { 1 }.effect()
      }

    return loop(0).unsafeRunSync()
  }

}
