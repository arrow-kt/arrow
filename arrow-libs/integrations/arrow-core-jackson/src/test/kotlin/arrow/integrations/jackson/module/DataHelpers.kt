package arrow.integrations.jackson.module

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

data class SomeObject(val someString: String, val someInt: Int)

fun Arb.Companion.someObject(): Arb<SomeObject> =
  Arb.bind(Arb.string(), Arb.int()) { str, int -> SomeObject(str, int) }
