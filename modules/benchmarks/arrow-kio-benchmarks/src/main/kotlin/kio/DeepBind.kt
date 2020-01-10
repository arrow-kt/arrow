package arrow.benchmarks.effects.kio

import it.msec.kio.UIO
import it.msec.kio.effect
import it.msec.kio.flatMap
import it.msec.kio.runtime.Runtime

object DeepBind {

  fun loop(n: Int): UIO<Int> =
    if (n <= 1) effect { n }
    else loop(n - 1).flatMap { a ->
      loop(n - 2).flatMap { b -> effect { a + b } }
    }

  fun fib(depth: Int) = Runtime.unsafeRunSync(loop(depth))
}
