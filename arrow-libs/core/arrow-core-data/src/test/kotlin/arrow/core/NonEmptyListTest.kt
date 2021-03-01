package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.laws.SemigroupLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroup
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    testLaws(
      SemigroupLaws.laws(Semigroup.nonEmptyList(), Gen.nonEmptyList(Gen.int()), Eq.any()),
    )

    "can align lists with different lengths" {
      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        a.align(b).size == max(a.size, b.size)
      }

      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        a.align(b).all.take(min(a.size, b.size)).all {
          it.isBoth
        }
      }
    }
  }
}
