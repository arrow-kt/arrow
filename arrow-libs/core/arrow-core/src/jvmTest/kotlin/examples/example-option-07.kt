// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption07

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

val someValue: Option<Double> = Some(20.0)
val value = when(someValue) {
 is Some -> someValue.value
 is None -> 0.0
}
fun main () {
 println("value = $value")
}
