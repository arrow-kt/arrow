package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object Delay extends RTS {

  def ioDelayLoop(size: Int, i: Int): IO[Nothing, Int] =
    IO.sync(i).flatMap { j =>
      if (j > size) IO.sync(j) else ioDelayLoop(size, j + 1)
    }

  def unsafeIODelayLoop(size: Int, i: Int): Int =
    unsafeRun(ioDelayLoop(size, i))

}
