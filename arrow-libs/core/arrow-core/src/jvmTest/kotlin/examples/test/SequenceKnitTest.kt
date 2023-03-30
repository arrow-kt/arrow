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

  "ExampleSequence03" {
    arrow.core.examples.exampleSequence03.test()
  }

  "ExampleSequence10" {
    arrow.core.examples.exampleSequence10.test()
  }

  "ExampleSequence17" {
    arrow.core.examples.exampleSequence17.test()
  }

}) {
  override fun timeout(): Long = 1000
}
