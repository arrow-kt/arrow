// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption20

import arrow.core.Some
import arrow.core.none

fun main() {
  Some(12).onSome { println("flower") } // Result: prints "flower" and returns: Some(12)
  none<Int>().onSome { println("flower") }  // Result: None
}
