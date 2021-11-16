// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist01

import arrow.core.nonEmptyListOf

val value =
 // nonEmptyListOf() // does not compile
 nonEmptyListOf(1, 2, 3, 4, 5) // NonEmptyList<Int>
fun main() {
 println(value)
}
