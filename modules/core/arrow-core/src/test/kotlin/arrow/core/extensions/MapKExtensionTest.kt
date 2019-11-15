package arrow.core.extensions

import arrow.Kind
import arrow.core.Ior
import arrow.core.MapK
import arrow.core.MapKPartialOf
import arrow.core.extensions.mapk.eq.eq
import arrow.core.extensions.mapk.foldable.foldable
import arrow.core.extensions.mapk.semialign.semialign
import arrow.core.k
import arrow.test.UnitSpec
import arrow.test.generators.mapK
import arrow.test.laws.SemialignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class MapKExtensionTest : UnitSpec() {
  init {
    testLaws(SemialignLaws.laws(MapK.semialign(),
      Gen.mapK(Gen.string(), Gen.int()) as Gen<Kind<MapKPartialOf<String>, Int>>,
      { MapK.eq(String.eq(), it) as Eq<Kind<MapKPartialOf<String>, *>> },
      MapK.foldable<String>()
    ))

    "can zip maps" {
      MapK.semialign<String>().run {
        val leftMap = mapOf("A" to 1, "B" to 2).k()
        val rightMap = mapOf("A" to 10, "C" to 50).k()

        val resultMap = mapOf(
          "A" to Ior.Both(1, 10),
          "B" to Ior.Left(2),
          "C" to Ior.Right(50))

        align(leftMap, rightMap) shouldBe resultMap
      }
    }
  }
}
