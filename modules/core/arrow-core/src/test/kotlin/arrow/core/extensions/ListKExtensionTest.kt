package arrow.core.extensions

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Ior
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.foldable.foldable
import arrow.core.extensions.listk.semialign.semialign
import arrow.core.k
import arrow.test.UnitSpec
import arrow.test.generators.listK
import arrow.test.laws.SemialignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class ListKExtensionTest : UnitSpec() {
  init {
    testLaws(SemialignLaws.laws(ListK.semialign(),
      Gen.listK(Gen.int()) as Gen<Kind<ForListK, Int>>,
      { ListK.eq(it) as Eq<Kind<ForListK, *>> },
      ListK.foldable()
    ))

    "lists with different lengths are padded" {
      ListK.semialign().align(
        listOf("A").k(),
        listOf("B", "C").k()
      ) shouldBe listOf(Ior.Both("A", "B"), Ior.Right("C")).k()
    }
  }
}
