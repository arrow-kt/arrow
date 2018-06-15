package arrow.instances

import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.ShowLaws
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(String.eq()) { it.toString() },
      ShowLaws.laws(String.show(), String.eq()) { it.toString() }
    )
  }
}
