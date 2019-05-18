package arrow.benchmarks.effects.scala.cats

import cats.effect.IO

object Map {

  def catsIOMapTest(iterations: Int, batch: Int): Long = {
    val f = { x: Int => x + 1 }
    var fx = IO.pure(0)

    var j = 0
    while (j < batch) {
      fx = fx.map(f)
      j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += fx.unsafeRunSync()
      i += 1
    }
    sum
  }

}
