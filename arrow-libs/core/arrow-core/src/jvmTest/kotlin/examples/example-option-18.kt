// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption18

import arrow.core.computations.option
import arrow.core.Some
import arrow.core.none
import arrow.core.Option

suspend fun value(): Option<Int> =
 option {
   val x = none<Int>().bind()
   val y = Some(1 + x).bind()
   val z = Some(1 + y).bind()
   x + y + z
 }
suspend fun main() {
 println(value())
}
