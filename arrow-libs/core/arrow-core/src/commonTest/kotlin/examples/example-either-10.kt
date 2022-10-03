// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither10

import arrow.core.Either
import arrow.core.flatMap

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Exception, String> =
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }

val x = magic("2")
val value = when(x) {
  is Either.Left -> when (x.value) {
    is NumberFormatException -> "Not a number!"
    is IllegalArgumentException -> "Can't take reciprocal of 0!"
    else -> "Unknown error"
  }
  is Either.Right -> "Got reciprocal: ${x.value}"
}
fun main() {
 println("value = $value")
}
