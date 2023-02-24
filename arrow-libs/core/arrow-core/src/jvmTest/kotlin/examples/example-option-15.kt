// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption15

import arrow.core.*

fun main() {
val value =
 //sampleStart
   Some(1).map { it + 1 }
 //sampleEnd
 println(value)
}
