package arrow.typeclasses

import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.sequenceEither
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TraverseTest : StringSpec({
  "traverse is stacksafe over very long collections and short circuits properly" {
    // This has to traverse 50k elements till it reaches None and terminates
    generateSequence(0) { it + 1 }.map { if (it < 50_000) Right(it) else Left(Unit) }
      .sequenceEither() shouldBe Left(Unit)
  }
})
