package arrow.benchmarks

import arrow.benchmarks.effects.scala.zio.ZIORTS
import arrow.effects.IO
import arrow.effects.suspended.fx.Fx
import org.openjdk.jmh.annotations.*
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.fx.unsafeRun.runBlocking as fxRunBlocking
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class DeepBind {

  @Param("20")
  var depth: Int = 0

  fun ioFib(n: Int): IO<Int> =
    if (n <= 1) IO { n }
    else ioFib(n - 1).flatMap { a ->
      ioFib(n - 2).flatMap { b -> IO { a + b } }
    }

  fun fxFib(n: Int): Fx<Int> =
    if (n <= 1) Fx { n }
    else fxFib(n - 1).flatMap { a ->
      fxFib(n - 2).flatMap { b -> Fx { a + b } }
    }

  @Benchmark
  fun fx(): Int =
    Fx.unsafeRunBlocking(fxFib(depth))

  @Benchmark
  fun io(): Int =
    ioFib(depth).unsafeRunSync()

  @Benchmark
  fun cats(): Any =
    arrow.benchmarks.effects.scala.cats.`DeepBind$`.`MODULE$`.fib(depth).unsafeRunSync()

  @Benchmark
  fun zio(): Any =
    arrow.benchmarks.effects.scala.zio.`DeepBind$`.`MODULE$`.fib(depth)

}