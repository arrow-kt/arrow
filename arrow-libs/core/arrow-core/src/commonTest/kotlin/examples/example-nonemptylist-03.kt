// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist03

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

fun sumNel(nel: NonEmptyList<Int>): Int =
 nel.foldLeft(0) { acc, n -> acc + n }
val value = sumNel(nonEmptyListOf(1, 1, 1, 1))
fun main() {
 println("value = $value")
}
