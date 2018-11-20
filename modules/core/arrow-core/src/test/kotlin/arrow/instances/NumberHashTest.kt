package arrow.instances

import arrow.test.UnitSpec
import arrow.test.laws.HashLaws
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
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