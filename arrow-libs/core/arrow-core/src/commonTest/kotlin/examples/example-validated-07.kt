// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated07

import arrow.core.Validated
fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<E, C> {
 return when {
  v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.value, v2.value))
  v1 is Validated.Valid && v2 is Validated.Invalid -> v2
  v1 is Validated.Invalid && v2 is Validated.Valid -> v1
  v1 is Validated.Invalid && v2 is Validated.Invalid -> TODO()
  else -> TODO()
 }
}
