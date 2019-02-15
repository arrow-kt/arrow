package arrow.benchmarks.effects

import arrow.effects.IO
import arrow.effects.extensions.io.async.shift
import arrow.effects.extensions.io.monadDefer.binding
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
import kotlinx.coroutines.Dispatchers
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit


@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class IODeepBindBenchmark {

  @Param("100000")
  var size: Int = 0

  @Benchmark
  fun pure(): Int {
    fun loop(i: Int): IO<Int> =
      binding {
        val j = bind { IO.just(i) }
        bind { if (j > size) IO.just(j) else loop(j + 1) }
      }

    return unsafe { runBlocking { loop(0) } }
  }

  @Benchmark
  fun delay(): Int {
    fun loop(i: Int): IO<Int> =
      binding {
        val j = bind { IO { i } }
        bind { if (j > size) IO { j } else loop(j + 1) }
      }

    return unsafe { runBlocking { loop(0) } }
  }

  @Benchmark
  fun async(): Int {
    fun loop(i: Int): IO<Int> =
      binding {
        val j = bind { IO { i } }
        bind { Dispatchers.Default.shift() }
        bind { if (j > size) IO { j } else loop(j + 1) }
      }

    return unsafe { runBlocking { loop(0) } }
  }

}
