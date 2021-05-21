package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class ListKTest : UnitSpec() {

  init {

    testLaws(MonoidLaws.laws(Monoid.list(), Arb.list(Arb.int())))

    "mapNotNull() should map list and filter out null values" {
      checkAll(Arb.list(Arb.int())) { listk ->
        listk.mapNotNull {
          when (it % 2 == 0) {
            true -> it.toString()
            else -> null
          }
        } shouldBe listk.toList().filter { it % 2 == 0 }.map { it.toString() }
      }
    }
  }
}
