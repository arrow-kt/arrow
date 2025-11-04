@file:Suppress("API_NOT_AVAILABLE")

package arrow.core.test

import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds

interface LawSet {
  val laws: List<Law>
}

data class Law(val name: String, val test: suspend () -> Any?)

@IgnorableReturnValue
fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { x, y -> x == y }): Boolean =
  if (f(this, b)) true else fail("Found $this but expected: $b")

fun testLaws(lawSet: LawSet): TestResult = testLaws(lawSet.laws)

fun testLaws(vararg laws: List<Law>): TestResult = runTest(timeout = (30 * laws.size).seconds) {
  laws
    .flatMap(List<Law>::asIterable)
    .distinctBy(Law::name)
    .forEach { law: Law -> val _ = law.test() }
}

