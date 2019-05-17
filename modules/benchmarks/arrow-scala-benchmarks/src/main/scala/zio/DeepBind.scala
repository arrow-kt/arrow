package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object DeepBind {

  def loop(n: Int): Task[BigInt] =
    if (n <= 1) ZIO.effect[BigInt](n)
    else
      loop(n - 1).flatMap { a =>
        loop(n - 2).flatMap(b => ZIO.effect(a + b))
      }

  def fib(depth: Int): BigInt = ZIORTS.unsafeRun(loop(depth))
}
