// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption08

import arrow.core.*

val noValue: Option<Double> = None
val value = noValue.fold(
 { 0.0 },
 { it }
)
fun main () {
 println("value = $value")
}
