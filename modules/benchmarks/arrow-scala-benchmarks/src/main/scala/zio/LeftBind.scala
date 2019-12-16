package arrow.benchmarks.effects.scala.zio

import zio._

object LeftBind {


  def loop(depth: Int, size: Int, i: Int): UIO[Int] =
    if (i % depth == 0) IO.effectTotal[Int](i + 1).flatMap { loop(depth, size, _) }
    else if (i < size) loop(depth, size, i + 1).flatMap(i => IO.effectTotal(i))
    else IO.effectTotal(i)

  def unsafeIOLeftBindLoop(depth: Int, size: Int, i: Int): Int =
    ZIORTS.unsafeRun(loop(depth, size, i))

}