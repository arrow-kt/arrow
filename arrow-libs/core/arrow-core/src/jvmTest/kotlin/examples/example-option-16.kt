// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption16

import arrow.core.*

 val value =
 Some(1).zip(Some("Hello"), Some(20.0), ::Triple)
fun main() {
 println(value)
}
