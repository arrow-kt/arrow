// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption01

import arrow.core.Option
import arrow.core.Some
import arrow.core.none

val someValue: Option<String> = Some("I am wrapped in something")
val emptyValue: Option<String> = none()
fun main() {
 println("value = $someValue")
 println("emptyValue = $emptyValue")
}
