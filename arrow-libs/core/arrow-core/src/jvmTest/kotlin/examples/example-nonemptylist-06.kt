// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist06

import arrow.*
import arrow.core.*
import arrow.core.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import arrow.core.zip
import java.util.UUID

data class Person(val id: UUID, val name: String, val year: Int)

// Note each NonEmptyList is of a different type
val nelId: NonEmptyList<UUID> = nonEmptyListOf(UUID.randomUUID(), UUID.randomUUID())
val nelName: NonEmptyList<String> = nonEmptyListOf("William Alvin Howard", "Haskell Curry")
val nelYear: NonEmptyList<Int> = nonEmptyListOf(1926, 1900)

val value = nelId.zip(nelName, nelYear) { id, name, year ->
 Person(id, name, year)
}
fun main() {
 println("value = $value")
}
