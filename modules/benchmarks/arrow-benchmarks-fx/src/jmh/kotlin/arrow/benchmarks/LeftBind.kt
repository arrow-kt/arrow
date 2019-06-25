package arrow.benchmarks

import arrow.fx.IO
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
open class LeftBind {

  @Param("10000")
  var size: Int = 0

  @Param("100")
  var depth: Int = 0

  fun ioLoop(i: Int): IO<Int> =
    if (i % depth == 0) IO { i + 1 }.flatMap { ioLoop(it) }
    else if (i < size) ioLoop(i + 1).flatMap { IO.just(it) }
    else IO.just(i)

  @Benchmark
  fun io(): Int =
    IO.just(0).flatMap { ioLoop(it) }.unsafeRunSync()

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`LeftBind$`.`MODULE$`.unsafeIOLeftBindLoop(depth, size, 0)

  @Benchmark
  fun scalazZIO(): Int =
    arrow.benchmarks.effects.scala.zio.`LeftBind$`.`MODULE$`.unsafeIOLeftBindLoop(depth, size, 0)

      // RxJava & Reactor are not stack-safe and overflow in the benchmark.

//    fun monoLoop(i: Int): Mono<Int> =
//    if (i % depth == 0) Mono.fromSupplier { i + 1 }.flatMap { monoLoop(it) }
//    else if (i < size) monoLoop(i + 1).flatMap { ii -> Mono.fromSupplier { ii } }
//    else Mono.fromSupplier { i }

//    fun singleLoop(i: Int): Single<Int> =
//    if (i % depth == 0) Single.fromCallable { i + 1 }.flatMap { singleLoop(it) }
//    else if (i < size) singleLoop(i + 1).flatMap { i -> Single.just(i) }
//    else Single.just(i)

//    @Benchmark
//  fun mono(): Int =
//    Mono.fromSupplier { 0 }.flatMap { monoLoop(it) }.block()!!

//  @Benchmark
//  fun single(): Int =
//    Single.just(0).flatMap { singleLoop(it) }.blockingGet()
}
