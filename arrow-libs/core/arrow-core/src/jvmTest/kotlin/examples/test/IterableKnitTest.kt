// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.test

import io.kotest.core.spec.style.StringSpec

class IterableKnitTest : StringSpec({
  "ExampleIterable01" {
    arrow.core.examples.exampleIterable01.test()
  }

  "ExampleIterable02" {
    arrow.core.examples.exampleIterable02.test()
  }

}) {
  override fun timeout(): Long = 1000
}
