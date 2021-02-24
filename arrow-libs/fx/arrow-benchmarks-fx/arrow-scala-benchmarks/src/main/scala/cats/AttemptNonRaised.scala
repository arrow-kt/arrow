package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object AttemptNonRaised {

  def ioLoopHappy(size: Int, i: Int): IO[Int] =
    if (i < size) {
      IO {
        i + 1
      }.attempt
        .flatMap { result =>
          result.fold[IO[Int]](IO.raiseError[Int], ioLoopHappy(size, _))
        }
    } else IO.pure(1)

}
