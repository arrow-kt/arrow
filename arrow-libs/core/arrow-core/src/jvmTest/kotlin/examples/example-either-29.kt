// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither29

import arrow.core.Either.Left
import arrow.core.leftIfNull

val value =
 Left(12).leftIfNull({ -1 })
fun main() {
 println(value)
}
