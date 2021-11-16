// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption08

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

val noValue: Option<Double> = None
val value = when(noValue) {
 is Some -> noValue.value
 is None -> 0.0
}
fun main () {
 println("value = $value")
}
