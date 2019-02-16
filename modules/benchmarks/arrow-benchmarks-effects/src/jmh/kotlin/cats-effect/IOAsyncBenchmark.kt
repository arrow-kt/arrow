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

  fun asyncLoop(i: Int): IO<Int> =
    if (i < size) evalAsync(i + 1).flatMap { asyncLoop(it) }
    else evalAsync(i)

  @Benchmark
  fun async(): Int =
    asyncLoop(0).unsafeRunSync()

  fun cancelableLoop(i: Int): IO<Int> =
    if (i < size) evalCancelable(i + 1).flatMap { cancelableLoop(it) }
    else evalCancelable(i)

  @Benchmark
  fun cancelable(): Int =
    cancelableLoop(0).unsafeRunSync()

  fun parMap2Task(): IO<Int> = (0 until size).toList().foldLeft(IO { 0 }) { acc, i ->
    NonBlocking.parMapN(acc, IO { i }) { a, b -> a + b }
  }

  @Benchmark
  fun parMap2(): Int =
    parMap2Task().unsafeRunSync()

  fun raicePairTask(): IO<Int> = (0 until size).toList().foldLeft(IO{0}) { acc, _ ->
    IO.racePair(NonBlocking, acc, IO { 1 }).flatMap { ei ->
      when (ei) {
        is Either.Left -> ei.a.b.cancel().map { ei.a.a }
        is Either.Right ->  ei.b.a.cancel().map { ei.b.b }
      }
    }
  }

  @Benchmark
  fun racePair(): Int =
    raicePairTask().unsafeRunSync()

  fun startLoop(i: Int): IO<Int> =
    if (i < size)
      (IO { i + 1 }).startFiber(NonBlocking).flatMap{it.join()}.flatMap { startLoop(it) }
    else
      IO.just(i)

  @Benchmark
  fun start(): Int =
    startLoop(0).unsafeRunSync()

//  fun uncancelableLoop(i: Int): IO<Int> =
//    if (i < size)
//      IO { i + 1 }.uncancelable().flatMap { uncancelableLoop(it) }
//    else
//      IO.just(i)
//
//  @Benchmark
//  fun uncancelable(): Int =
//    uncancelableLoop(0).unsafeRunSync()

  fun bracketLoop(i: Int): IO<Int> =
    if (i < size)
      IO { i }.bracket({ IO.unit }, { ib -> IO { ib + 1 } }).flatMap { bracketLoop(it) }
    else
      IO.just(i)

  @Benchmark
  fun bracket(): Int =
    bracketLoop(0).unsafeRunSync()

}