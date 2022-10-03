// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist01

import arrow.core.nonEmptyListOf
import arrow.core.toNonEmptyListOrNull

fun main() {
 println(nonEmptyListOf(1, 2, 3, 4, 5))
 println(listOf(1, 2, 3).toNonEmptyListOrNull())
 println(emptyList<Int>().toNonEmptyListOrNull())
}
