// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither27

import arrow.core.Either.Right
import arrow.core.leftIfNull

val value =
 Right(null).leftIfNull({ -1 })
fun main() {
 println(value)
}
