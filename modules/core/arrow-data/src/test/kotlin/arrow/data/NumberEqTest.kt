package arrow.data

import arrow.instances.*
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NumberEqTest : UnitSpec() {
  init {

    testLaws(
      EqLaws.laws(LongEqInstance) { it.toLong() },
      EqLaws.laws(IntEqInstance) { it },
      EqLaws.laws(DoubleEqInstance) { it.toDouble() },
      EqLaws.laws(FloatEqInstance) { it.toFloat() },
      EqLaws.laws(ByteEqInstance) { it.toByte() },
      EqLaws.laws(ShortEqInstance) { it.toShort() }
    )

  }
}
