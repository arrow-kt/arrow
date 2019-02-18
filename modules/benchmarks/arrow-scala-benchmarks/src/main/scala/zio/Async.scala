package arrow.benchmarks.effects.scala.zio

import scalaz.zio._
import scalaz.zio.internal.Executor

import scala.concurrent.ExecutionContext

object Async extends RTS {

  val executor: Executor =
    Executor.fromExecutionContext(Executor.Yielding, Int.MaxValue)(ExecutionContext.global)

  def ioAsyncLoop(size: Int, i: Int): IO[Nothing, Int] =
    IO.lock(executor)(if (i > size) IO.sync(i) else ioAsyncLoop(size, i + 1))

  def unsafeIOAsyncLoop(size: Int, i: Int): Int =
    unsafeRun(ioAsyncLoop(size, i))

}
