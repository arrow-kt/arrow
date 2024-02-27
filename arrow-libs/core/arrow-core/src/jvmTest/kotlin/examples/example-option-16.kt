// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption16

import arrow.core.Some
import arrow.core.none

fun main() {
  Some(12).onNone { println("flower") } // Result: Some(12)
  none<Int>().onNone { println("flower") }  // Result: prints "flower" and returns: None
}
