package arrow.core.usage

import arrow.core.Either
import arrow.core.raise.context.*

// this file checks that everything still resolves
// once the context parameter versions are used

fun example1(x: Int): Either<String, Int> = either {
  ensure(x > 0) { "x is not positive" }
  x + 1
}

context(_: Raise<String>)
fun example2a(x: Int): Int {
  ensure(x > 0) { "x is not positive" }
  return x + 1
}

fun example2b(x: Int): Either<String, Int> = either { example2a(x) }

context(_: Raise<String>)
fun example3(x: Int): Int =
  withError(transform = { it.uppercase() }) {
    example1(x).bind()
  }

context(_: Raise<String>)
fun example3a(x: Int): Int =
  withError(transform = { it.uppercase() }) {
    example2a(x)
  }

context(_: Raise<String>)
fun example3b(x: Int): Int =
  withError(transform = { it.uppercase() }) {
    example2b(x).bind()
  }
