// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption20

import arrow.core.Some
import arrow.core.None
import arrow.core.Option

fun main() {
  Some(12).exists { it > 10 } // Result: 12
  Some(7).exists { it > 10 }  // Result: null

  val none: Option<Int> = None
  none.exists { it > 10 }      // Result: null
}
