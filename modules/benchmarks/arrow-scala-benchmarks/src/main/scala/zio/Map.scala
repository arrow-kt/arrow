package arrow.benchmarks.effects.scala.zio

import scalaz.zio._

object Map extends RTS {

  def zioMapTest(iterations: Int, batch: Int): Long = {
    val f = { x: Int => x + 1 }
    var fx = IO.succeed(0)

    var j = 0
    while (j < batch) {
      fx = fx.map(f)
      j += 1
    }

    var sum = 0L
    var i = 0
    while (i < iterations) {
      sum += unsafeRun(fx)
      i += 1
    }
    sum
  }

}
