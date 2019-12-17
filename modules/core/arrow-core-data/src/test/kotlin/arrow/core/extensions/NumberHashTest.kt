package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.generators.byte
import arrow.test.generators.short
import arrow.test.laws.HashLaws
import io.kotlintest.properties.Gen

class NumberHashTest : UnitSpec() {
  init {

    testLaws(
      HashLaws.laws(Long.hash(), Long.eq(), Gen.long()),
      HashLaws.laws(Int.hash(), Int.eq(), Gen.int()),
      HashLaws.laws(Double.hash(), Double.eq(), Gen.double()),
      HashLaws.laws(Float.hash(), Float.eq(), Gen.float()),
      HashLaws.laws(Byte.hash(), Byte.eq(), Gen.byte()),
      HashLaws.laws(Short.hash(), Short.eq(), Gen.short())
    )
  }
}
