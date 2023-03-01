// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption27

import arrow.core.Option
import arrow.core.none
import arrow.core.Some
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun test() {
  val error: Option<Int> = none()
  fun fallback(): Option<Int> = Some(5)
  fun failure(): Option<Int> = none()

  error.recover { fallback().bind() } shouldBe Some(5)
  error.recover { failure().bind() } shouldBe none()
}
