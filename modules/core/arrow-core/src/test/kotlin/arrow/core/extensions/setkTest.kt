package arrow.core.extensions

import arrow.core.SetK
import arrow.core.extensions.setk.monoid.monoid
import arrow.core.k
import arrow.test.UnitSpec
import io.kotlintest.shouldBe

class setkTests: UnitSpec() {
  init {
    "SetK combineAll leads to StackOverflow #1717" {
      SetK.monoid<Int>().run {
        listOf(SetK.just(1), SetK.just(2)).combineAll() shouldBe setOf(1, 2).k()
      }
    }
  }
}
