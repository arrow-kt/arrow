// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist06

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

data class Person(val id: Long, val name: String, val year: Int)

// Note each NonEmptyList is of a different type
val nelId: NonEmptyList<Long> = nonEmptyListOf(Random.nextLong() Random.nextLong())
val nelName: NonEmptyList<String> = nonEmptyListOf("William Alvin Howard", "Haskell Curry")
val nelYear: NonEmptyList<Int> = nonEmptyListOf(1926, 1900)

val value = nelId.zip(nelName, nelYear) { id, name, year ->
 Person(id, name, year)
}
fun main() {
 println("value = $value")
}
