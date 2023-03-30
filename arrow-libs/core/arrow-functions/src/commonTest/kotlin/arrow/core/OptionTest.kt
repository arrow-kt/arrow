package arrow.core

import arrow.core.raise.option
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.option
import arrow.core.test.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

// Utils

private fun <T> iterableOf(vararg elements: T): Iterable<T> = Iterable { iterator { yieldAll(elements.toList()) } }
