// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated08

import arrow.core.NonEmptyList
import arrow.core.Validated
fun <E, A, B, C> parallelValidate
  (v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<NonEmptyList<E>, C> =
 when {
  v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.value, v2.value))
  v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
  v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
  v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.value, listOf(v2.value)))
  else -> throw IllegalStateException("Not possible value")
 }
