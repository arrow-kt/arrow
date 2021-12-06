// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither06

import arrow.*
import arrow.core.*
import arrow.core.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

import arrow.core.Either
import arrow.core.flatMap

fun parse(s: String): Int =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt()
  else throw NumberFormatException("$s is not a valid integer.")

fun reciprocal(i: Int): Double =
  if (i == 0) throw IllegalArgumentException("Cannot take reciprocal of 0.")
  else 1.0 / i

fun stringify(d: Double): String = d.toString()
