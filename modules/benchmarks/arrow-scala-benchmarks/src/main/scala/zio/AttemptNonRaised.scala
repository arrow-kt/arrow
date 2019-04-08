package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object AttemptNonRaised {

  def ioLoopHappy(size: Int, i: Int): IO[Nothing, Int] =
    if (i < size) {
      IO.sync {
        i + 1
      }.attempt.flatMap { result =>
          result.fold[IO[Nothing, Int]](IO.fail, ioLoopHappy(size, _))
        }
    } else IO.succeed(1)

}
