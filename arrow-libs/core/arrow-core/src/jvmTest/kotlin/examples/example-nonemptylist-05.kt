// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist05

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

val nelOne: NonEmptyList<Int> = nonEmptyListOf(1, 2, 3)
val nelTwo: NonEmptyList<Int> = nonEmptyListOf(4, 5)

val value = nelOne.flatMap { one ->
 nelTwo.map { two ->
   one + two
 }
}
fun main() {
 println("value = $value")
}
