package arrow.benchmarks

import arrow.fx.ConcurrentQueue
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.Queue
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.unit
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.fix
import arrow.fx.internal.CancelableQueue
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class Queue {

  @Param("1000")
  var size: Int = 0

  var ConcurQueue by Delegates.notNull<Queue<ForIO, Int>>()
  var CancelQueue by Delegates.notNull<Queue<ForIO, Int>>()

  @Setup(Level.Trial)
  fun createQueues(): Unit {
    ConcurQueue = ConcurrentQueue.empty<ForIO, Int>(IO.concurrent()).fix().unsafeRunSync()
    CancelQueue = CancelableQueue.empty<ForIO, Int>(IO.concurrent()).fix().unsafeRunSync()
  }

  fun <A> IOOf<A>.repeat(n: Int): IO<A> =
    if (n < 1) fix() else flatMap { repeat(n - 1) }

  fun loop(q: Queue<ForIO, Int>): Unit =
    q.offer(0).unit().repeat(size).flatMap {
      q.take().unit().repeat(size)
    }.unsafeRunSync()

  @Benchmark
  fun concurrentQueue(): Unit = loop(ConcurQueue)

  @Benchmark
  fun cancelableQueue(): Unit = loop(CancelQueue)
}
