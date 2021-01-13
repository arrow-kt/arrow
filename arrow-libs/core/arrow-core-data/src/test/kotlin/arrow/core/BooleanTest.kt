package arrow.core

import arrow.core.extensions.boolean
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.order
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.ShowLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.boolean(), Gen.bool(), Boolean.eq()),
      EqLaws.laws(Boolean.eq(), Gen.bool()),
      ShowLaws.laws(Boolean.show(), Boolean.eq(), Gen.bool()),
      HashLaws.laws(Boolean.hash(), Gen.bool(), Boolean.eq()),
      OrderLaws.laws(Boolean.order(), Gen.bool())
    )
  }
}
