// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither11

import arrow.core.Either
import arrow.core.flatMap

sealed class Error {
 object NotANumber : Error()
 object NoZeroReciprocal : Error()
}

fun parse(s: String): Either<Error, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(Error.NotANumber)

fun reciprocal(i: Int): Either<Error, Double> =
  if (i == 0) Either.Left(Error.NoZeroReciprocal)
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Error, String> =
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }

val x = magic("2")
val value = when(x) {
  is Either.Left -> when (x.value) {
    is Error.NotANumber -> "Not a number!"
    is Error.NoZeroReciprocal -> "Can't take reciprocal of 0!"
  }
  is Either.Right -> "Got reciprocal: ${x.value}"
}
fun main() {
 println("value = $value")
}
