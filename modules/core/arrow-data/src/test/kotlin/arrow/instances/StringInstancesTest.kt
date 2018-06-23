package arrow.instances

import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.ShowLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(String.eq()) { it.toString() },
      ShowLaws.laws(String.show(), String.eq()) { it.toString() }
    )
  }
}
