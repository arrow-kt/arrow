package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class ListKTest : UnitSpec() {

  init {

    testLaws(MonoidLaws.laws(Monoid.list(), Gen.list(Gen.int())))

    "mapNotNull() should map list and filter out null values" {
      forAll(Gen.list(Gen.int())) { listk ->
        listk.mapNotNull {
          when (it % 2 == 0) {
            true -> it.toString()
            else -> null
          }
        } == listk.toList().filter { it % 2 == 0 }.map { it.toString() }
      }
    }
  }
}
