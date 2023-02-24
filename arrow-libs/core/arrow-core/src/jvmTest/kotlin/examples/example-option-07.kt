// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption07

import arrow.core.*

val someValue: Option<Double> = Some(20.0)
val value = someValue.fold(
 { 0.0 },
 { it }
)
fun main () {
 println("value = $value")
}
