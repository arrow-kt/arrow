package arrow.benchmarks.effects

import arrow.core.Either
import arrow.core.Right
import arrow.core.Tuple2
import arrow.data.extensions.list.foldable.foldLeft
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.map
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.concurrent.parMapN
import arrow.effects.fix
import arrow.effects.racePair
import arrow.effects.startFiber
import arrow.effects.typeclasses.suspended.NonBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * To do comparative benchmarks between versions:
 *
 *     benchmarks/run-benchmark AsyncBenchmark
 *
 * This will generate results in `benchmarks/results`.
 *
 * Or to run the benchmark from within SBT:
 *
 *     jmh:run -i 10 -wi 10 -f 2 -t 1 cats.effect.benchmarks.AsyncBenchmark
 *
 * Which means "10 iterations", "10 warm-up iterations", "2 forks", "1 thread".
 * Please note that benchmarks should be usually executed at least in
 * 10 iterations (as a rule of thumb), but more is better.
 */
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class IOAsyncBenchmark {

  @Param("100")
  var size: Int = 0

  fun evalAsync(n: Int): IO<Int> =
    IO.async { _, callback -> callback(Right(n)) }

  fun evalCancelable(n: Int): IO<Int> =
    IO.concurrent().cancelable<Int> { cb ->
      cb(Right(n))
      IO.unit
    }.fix()

  @Benchmark
  fun async() = {
    fun loop(i: Int): IO<Int> =
      if (i < size) evalAsync(i + 1).flatMap { loop(it) }
      else evalAsync(i)

    IO { 0 }.flatMap { loop(it) }.unsafeRunSync()
  }

  @Benchmark
  fun cancelable() = {
    fun loop(i: Int): IO<Int> =
      if (i < size) evalCancelable(i + 1).flatMap { loop(it) }
      else evalCancelable(i)

    IO { 0 }.flatMap { loop(it) }.unsafeRunSync()
  }

  @Benchmark
  fun parMap2() = {
    val task = (0 until size).toList().foldLeft(IO { 0 }) { acc, i ->
      NonBlocking.parMapN(acc, IO { i }) { a, b -> a + b }
    }
    task.unsafeRunSync()
  }

  @Benchmark
  fun racePair() = {
    val task = (0 until size).toList().foldLeft(IO{0}) { acc, _ ->
      IO.racePair(NonBlocking, acc, IO { 1 }).flatMap { ei ->
        when (ei) {
          is Either.Left -> ei.a.b.cancel().map { ei.a.a }
          is Either.Right ->  ei.b.a.cancel().map { ei.b.b }
        }
      }
    }

    task.unsafeRunSync()
  }

  @Benchmark
  fun start() = {
    fun loop(i: Int): IO<Int> =
      if (i < size)
        (IO { i + 1 }).startFiber(NonBlocking).flatMap{it.join()}.flatMap { loop(it) }
      else
        IO.just(i)

    IO { 0 }.flatMap { loop(it) }.unsafeRunSync()
  }

  @Benchmark
  fun uncancelable() = {
    fun loop(i: Int): IO<Int> =
      if (i < size)
        IO { i + 1 }.uncancelable().flatMap { loop(it) }
      else
        IO.just(i)

    IO { 0 }.flatMap { loop(it) }.unsafeRunSync()
  }

  @Benchmark
  fun bracket() = {
    fun loop(i: Int): IO<Int> =
      if (i < size)
        IO { i }.bracket({ IO.unit }, { ib -> IO { ib + 1 } }).flatMap { loop(it) }
      else
        IO.just(i)
    IO { 0 }.flatMap { loop(it) }.unsafeRunSync()
  }

}