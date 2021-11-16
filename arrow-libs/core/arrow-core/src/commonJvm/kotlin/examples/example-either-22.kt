// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither22

import arrow.core.Either

val value =
 Either.conditionally(true, { "Error" }, { 42 })
fun main() {
 println(value)
}
