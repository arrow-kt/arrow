// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither07

import arrow.core.Either

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

val notANumber = parse("Not a number")
val number2 = parse("2")
fun main() {
 println("notANumber = $notANumber")
 println("number2 = $number2")
}
