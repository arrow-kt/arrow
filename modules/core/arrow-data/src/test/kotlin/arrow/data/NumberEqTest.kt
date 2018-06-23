package arrow.data

import arrow.instances.*
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NumberEqTest : UnitSpec() {
  init {

    testLaws(
      EqLaws.laws(Long.eq()) { it.toLong() },
      EqLaws.laws(Int.eq()) { it },
      EqLaws.laws(Double.eq()) { it.toDouble() },
      EqLaws.laws(Float.eq()) { it.toFloat() },
      EqLaws.laws(Byte.eq()) { it.toByte() },
      EqLaws.laws(Short.eq()) { it.toShort() }
    )

  }
}
