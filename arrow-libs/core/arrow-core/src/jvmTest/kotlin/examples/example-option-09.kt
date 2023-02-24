// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption09

import arrow.core.*

val number: Option<Int> = Some(3)
val noNumber: Option<Int> = None
val mappedResult1 = number.map { it * 1.5 }
val mappedResult2 = noNumber.map { it * 1.5 }
fun main () {
 println("number = $number")
 println("noNumber = $noNumber")
 println("mappedResult1 = $mappedResult1")
 println("mappedResult2 = $mappedResult2")
}
