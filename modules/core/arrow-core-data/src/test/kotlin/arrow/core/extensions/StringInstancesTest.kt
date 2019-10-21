package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.laws.HashLaws
import arrow.test.laws.ShowLaws

class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      ShowLaws.laws(String.show(), String.eq()) { it.toString() },
      HashLaws.laws(String.hash(), String.eq()) { it.toString() }
    )
  }
}
