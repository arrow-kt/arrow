package arrow.effects

import arrow.effects.suspended.fx.Fx

private fun fxTest(iterations: Int, batch: Int): Long {
  val f = { x: Int -> x + 1 }
  var fx = Fx.just(0)

  var j = 0
  while (j < batch) {
    fx = fx.map(f); j += 1
  }

  var sum = 0L
  var i = 0
  while (i < iterations) {
    sum += Fx.unsafeRunBlocking(fx)
    i += 1
  }
  return sum
}

fun main() {
  fxTest(500000, 1)
}
