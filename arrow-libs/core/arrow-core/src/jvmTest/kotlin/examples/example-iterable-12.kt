// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable12

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
   listOf(1, 2, 3, 4).unalign {
     if(it % 2 == 0) it.rightIor()
     else it.leftIor()
   } shouldBe Pair(listOf(1, null, 3, null), listOf(null, 2, null, 4))
}
