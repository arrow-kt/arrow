package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object MapStream {

  def test(times: Int, batchSize: Int): Long = {
    var stream = range(0, times)
    var i = 0
    while (i < batchSize) {
      stream = mapStream(addOne)(stream)
      i += 1
    }
    sum(0)(stream).unsafeRunSync()
  }

  final case class Stream(value: Int, next: IO[Option[Stream]])

  val addOne = (x: Int) => x + 1

  def range(from: Int, until: Int): Option[Stream] =
    if (from < until)
      Some(Stream(from, IO(range(from + 1, until))))
    else
      None

  def mapStream(f: Int => Int)(box: Option[Stream]): Option[Stream] =
    box match {
      case Some(Stream(value, next)) =>
        Some(Stream(f(value), next.map(mapStream(f))))
      case None =>
        None
    }

  def sum(acc: Long)(box: Option[Stream]): IO[Long] =
    box match {
      case Some(Stream(value, next)) =>
        next.flatMap(sum(acc + value))
      case None =>
        IO.pure(acc)
    }
}
