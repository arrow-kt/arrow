package arrow.typeclasses

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.sequence.traverse.sequence
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TraverseTest : StringSpec({
  "traverse is stacksafe over very long collections and short circuits properly" {
    // This has to traverse 50k elements till it reaches None and terminates
    generateSequence(0) { it + 1 }.map { if (it < 50_000) Some(it) else None }
      .sequence(Option.applicative()) shouldBe None
  }
})
