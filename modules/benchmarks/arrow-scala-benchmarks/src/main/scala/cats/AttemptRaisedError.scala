package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object dummy extends RuntimeException {
  override def fillInStackTrace(): Throwable = this
}

object AttemptRaisedError {

  def ioLoopNotHappy(size: Int, i: Int): IO[Int] =
    if (i < size) {
      IO {
        throw dummy
      }.attempt.flatMap { it =>
        it.fold(_ => ioLoopNotHappy(size, i + 1), IO.pure)
      }
    } else IO.pure(1)

}
