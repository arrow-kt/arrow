package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.laws.HashLaws
import arrow.test.laws.ShowLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      ShowLaws.laws(String.show(), String.eq()) { it.toString() },
      HashLaws.laws(String.hash(), String.eq()) { it.toString() }
    )
  }
}
