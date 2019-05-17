package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object Pure {

  def ioPureLoop(size: Int, i: Int): IO[Int] =
    IO.pure(i).flatMap { j =>
      if (j > size) IO.pure(j) else ioPureLoop(size, j + 1)
    }

  def unsafeIOPureLoop(size: Int, i: Int): Int =
    ioPureLoop(size, i).unsafeRunSync()

}
