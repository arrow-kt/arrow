package arrow.core

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

private fun Arb.Companion.intOpenEnded() = Arb.int(Int.MIN_VALUE + 1, Int.MAX_VALUE - 1)
