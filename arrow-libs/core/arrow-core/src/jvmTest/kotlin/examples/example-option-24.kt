// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption24

import arrow.core.Some
import arrow.core.None
import arrow.core.Option

fun main() {
  Some(12).exists { it > 10 } // Result: true
  Some(7).exists { it > 10 }  // Result: false

  val none: Option<Int> = None
  none.exists { it > 10 }      // Result: false
}
