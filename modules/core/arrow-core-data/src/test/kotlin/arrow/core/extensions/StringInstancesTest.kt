package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.laws.HashLaws
import arrow.test.laws.ShowLaws
import io.kotlintest.properties.Gen

class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      ShowLaws.laws(String.show(), String.eq(), Gen.string()),
      HashLaws.laws(String.hash(), String.eq(), Gen.string())
    )
  }
}
