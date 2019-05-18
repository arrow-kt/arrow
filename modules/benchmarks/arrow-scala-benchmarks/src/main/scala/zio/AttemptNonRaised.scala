package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object AttemptNonRaised {

  def ioLoopHappy(size: Int, i: Int): Task[Int] =
    if (i < size) {
      IO.effect {
        i + 1
      }.either.flatMap { result =>
          result.fold[Task[Int]](IO.fail, ioLoopHappy(size, _))
        }
    } else IO.succeed(1)

  def run(size: Int) = ZIORTS.unsafeRun(ioLoopHappy(size, 0))

}
