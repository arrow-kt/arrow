// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption17

import arrow.core.computations.option
import arrow.core.Some
import arrow.core.Option

suspend fun value(): Option<Int> =
 option {
   val a = Some(1).bind()
   val b = Some(1 + a).bind()
   val c = Some(1 + b).bind()
   a + b + c
}
suspend fun main() {
 println(value())
}
