// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated04

sealed class Validated<out E, out A> {
 data class Valid<out A>(val a: A) : Validated<Nothing, A>()
 data class Invalid<out E>(val e: E) : Validated<E, Nothing>()
}
