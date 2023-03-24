// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable14

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
   listOf(1, 2, 3, 4).unalign {
     if(it % 2 == 0) it.rightIor()
     else it.leftIor()
   } shouldBe Pair(listOf(1, 3), listOf(2, 4))
}
