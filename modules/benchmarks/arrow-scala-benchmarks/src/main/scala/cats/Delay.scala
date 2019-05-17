package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object Delay {

  def ioDelayLoop(size: Int, i: Int): IO[Int] =
    IO(i).flatMap { j =>
      if (j > size) IO(j) else ioDelayLoop(size, j + 1)
    }

  def unsafeIODelayLoop(size: Int, i: Int): Int =
    ioDelayLoop(size, i).unsafeRunSync()

}
