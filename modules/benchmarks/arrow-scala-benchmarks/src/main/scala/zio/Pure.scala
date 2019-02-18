package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object Pure extends RTS {

  def ioPureLoop(size: Int, i: Int): IO[Nothing, Int] =
    IO.succeed(i).flatMap { j =>
      if (j > size) IO.succeed(j) else ioPureLoop(size, j + 1)
    }

  def unsafeIOPureLoop(size: Int, i: Int): Int =
    unsafeRun(ioPureLoop(size, i))

}
