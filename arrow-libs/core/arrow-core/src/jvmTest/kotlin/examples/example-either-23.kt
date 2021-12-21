// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither23

import arrow.core.Either

val value =
 Either.conditionally(false, { "Error" }, { 42 })
fun main() {
 println(value)
}
