package arrow.benchmarks.effects.scala.cats

import cats.effect.{ContextShift, IO}
import cats.implicits._

import scala.concurrent.ExecutionContext

object Async {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def ioAsyncLoop(size: Int, i: Int): IO[Int] =
    IO.shift *> (if (i > size) IO.pure(i) else ioAsyncLoop(size, i + 1))

  def unsafeIOAsyncLoop(size: Int, i: Int): Int =
    ioAsyncLoop(size, i).unsafeRunSync()

}
