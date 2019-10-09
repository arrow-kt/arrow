package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.laws.HashLaws

class NumberHashTest : UnitSpec() {
  init {

    testLaws(
      HashLaws.laws(Long.hash(), Long.eq()) { it.toLong() },
      HashLaws.laws(Int.hash(), Int.eq()) { it },
      HashLaws.laws(Double.hash(), Double.eq()) { it.toDouble() },
      HashLaws.laws(Float.hash(), Float.eq()) { it.toFloat() },
      HashLaws.laws(Byte.hash(), Byte.eq()) { it.toByte() },
      HashLaws.laws(Short.hash(), Short.eq()) { it.toShort() }
    )
  }
}
