package arrow.data

import arrow.instances.*
import arrow.test.UnitSpec
import arrow.test.laws.EqLaws

import org.junit.runner.RunWith


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
