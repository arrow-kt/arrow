// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption19

import arrow.core.Some
import arrow.core.None
import arrow.core.Option
import arrow.core.exists

fun main() {
  Some(12).exists { it > 10 } // Result: true
  Some(7).exists { it > 10 }  // Result: false

  val none: Option<Int> = None
  none.exists { it > 10 }      // Result: false
}
