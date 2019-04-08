package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object DeepBind {

  def fib(n: Int): IO[Nothing, BigInt] =
    if (n <= 1) IO.succeedLazy[BigInt](n)
    else
      fib(n - 1).flatMap { a =>
        fib(n - 2).flatMap(b => IO.succeedLazy(a + b))
      }

}
