// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.test

import io.kotest.core.spec.style.StringSpec

class SequenceKnitTest : StringSpec({
  "ExampleSequence01" {
    arrow.core.examples.exampleSequence01.test()
  }

  "ExampleSequence02" {
    arrow.core.examples.exampleSequence02.test()
  }

  "ExampleSequence04" {
    arrow.core.examples.exampleSequence04.test()
  }

  "ExampleSequence11" {
    arrow.core.examples.exampleSequence11.test()
  }

  "ExampleSequence18" {
    arrow.core.examples.exampleSequence18.test()
  }

}) {
  override fun timeout(): Long = 1000
}
