package arrow.benchmarks.effects.scala.zio

import scalaz.zio._
import scalaz.zio.internal.Executor

import scala.concurrent.ExecutionContext

object Async {

  val executor: Executor =
    Executor.fromExecutionContext(Int.MaxValue)(ExecutionContext.global)

  def ioAsyncLoop(size: Int, i: Int): Task[Int] =
    IO.lock(executor)(if (i > size) IO.succeed(i) else ioAsyncLoop(size, i + 1))

  def unsafeIOAsyncLoop(size: Int, i: Int): Int =
    ZIORTS.unsafeRun(ioAsyncLoop(size, i))

}
