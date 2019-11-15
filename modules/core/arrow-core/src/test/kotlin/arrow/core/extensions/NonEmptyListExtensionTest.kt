package arrow.core.extensions

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.semialign.semialign
import arrow.test.UnitSpec
import arrow.test.generators.nonEmptyList
import arrow.test.laws.SemialignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class NonEmptyListExtensionTest : UnitSpec() {
  init {
    testLaws(SemialignLaws.laws(NonEmptyList.semialign(),
      Gen.nonEmptyList(Gen.int()) as Gen<Kind<ForNonEmptyList, Int>>,
      { NonEmptyList.eq(it) as Eq<Kind<ForNonEmptyList, *>> },
      NonEmptyList.foldable()
    ))

    "lists with different lengths are padded" {
      NonEmptyList.semialign().align(
        NonEmptyList.of("A"),
        NonEmptyList.of("B", "C")
      ) shouldBe NonEmptyList.of(Ior.Both("A", "B"), Ior.Right("C"))
    }
  }
}
