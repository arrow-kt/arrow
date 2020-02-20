package arrow.benchmarks.effects.scala.cats

import cats.effect._

object LeftBind {

  def loop(depth: Int, size: Int, i: Int): IO[Int] =
    if (i % depth == 0) IO(i + 1).flatMap(loop(depth, size, _))
    else if (i < size) loop(depth, size, i + 1).flatMap(i => IO(i))
    else IO(i)

  def unsafeIOLeftBindLoop(depth: Int, size: Int, i: Int): Int =
    IO(0).flatMap(loop(depth, size, _)).unsafeRunSync

}
