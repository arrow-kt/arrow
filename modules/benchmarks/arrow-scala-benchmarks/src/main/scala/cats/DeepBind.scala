package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object DeepBind {

  def fib(n: Int): IO[BigInt] =
    if (n <= 1) IO(n)
    else
      fib(n - 1).flatMap { a =>
        fib(n - 2).flatMap(b => IO(a + b))
      }
}
