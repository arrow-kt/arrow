package arrow.benchmarks.effects

import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.right
import arrow.effects.IO
import arrow.effects.typeclasses.suspended.*
import arrow.unsafe
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import arrow.effects.extensions.io.unsafeRun.runBlocking as ioRunBlocking
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking as fxRunBlocking

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Pure {

  @Param("50000")
  var size: Int = 0

  tailrec suspend fun fxDirectPureLoop(i: Int): Int =
    if (i > size) i else fxDirectPureLoop(i + 1)

  @Benchmark
  fun fx_direct(): Int =
    unsafe { fxRunBlocking { Fx { fxDirectPureLoop(0) } } }

  @Benchmark
  fun kotlinx_coroutines(): Int =
    runBlocking { fxDirectPureLoop(0) }

  tailrec suspend fun fxPureLoop(i: Int): suspend () -> Int {
    val j = !just(i)
    return if (j > size) just(j) else fxPureLoop(j + 1)
  }

  @Benchmark
  fun fx(): Int =
    unsafe { fxRunBlocking { Fx { !fxPureLoop(0) } } }

  tailrec suspend fun <E> bioPureLoop(i: Int): Either<E, Int> {
    val j = !just(i)
    return if (j > size) j.right() else bioPureLoop(j + 1)
  }

  @Benchmark
  fun fx_bio(): Int =
    unsafe { fxRunBlocking { CatchFx { bioPureLoop<String>(0) }.toFx() }.getOrHandle { 0 } }

  tailrec suspend fun <R, E> rioPureLoop(i: Int): EnvFx<R, E, Int> {
    val j = just(i)()
    return if (j > size) EnvFx { j.right() } else rioPureLoop(j + 1)
  }

  @Benchmark
  fun fx_rio(): Int =
    unsafe {
      fxRunBlocking {
        Fx { rioPureLoop<Int, Int>(0).toFx(0)() }
      }.getOrHandle { 0 }
    }

  fun ioPureLoop(i: Int): IO<Int> =
    IO.just(i).flatMap { j ->
      if (j > size) IO.just(j) else ioPureLoop(j + 1)
    }

  @Benchmark
  fun io(): Int =
    unsafe { ioRunBlocking { ioPureLoop(0) } }

  @Benchmark
  fun cats_io(): Int =
    arrow.benchmarks.effects.scala.cats.`Pure$`.`MODULE$`.unsafeIOPureLoop(size, 0)

  @Benchmark
  fun zio(): Int =
    arrow.benchmarks.effects.scala.zio.`Pure$`.`MODULE$`.unsafeIOPureLoop(size, 0)

}
