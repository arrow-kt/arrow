// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither05

import arrow.core.Either
import arrow.core.flatMap

fun parse(s: String): Int =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt()
  else throw NumberFormatException("$s is not a valid integer.")

fun reciprocal(i: Int): Double =
  if (i == 0) throw IllegalArgumentException("Cannot take reciprocal of 0.")
  else 1.0 / i

fun stringify(d: Double): String = d.toString()
