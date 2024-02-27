// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption20

import arrow.core.Option
import arrow.core.none
import arrow.core.Some
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun test() {
  val error: Option<Int> = none()
  val fallback: Option<Int> = error.recover { 5 }
  fallback shouldBe Some(5)
}
