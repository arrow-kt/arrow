package arrow.core.extensions

import arrow.core.test.UnitSpec
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.ShowLaws
import io.kotlintest.properties.Gen

class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      ShowLaws.laws(String.show(), String.eq(), Gen.string()),
      HashLaws.laws(String.hash(), Gen.string(), String.eq())
    )
  }
}
