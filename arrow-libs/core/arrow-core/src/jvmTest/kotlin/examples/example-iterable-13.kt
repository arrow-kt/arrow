// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable13

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
   listOf(
     Pair("A", 1).bothIor(),
     Pair("B", 2).bothIor(),
     "C".leftIor()
   ).seperateIor() shouldBe Pair(listOf("A", "B", "C"), listOf(1, 2))
}
