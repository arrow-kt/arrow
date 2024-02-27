package arrow.optics.test.laws

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

interface LawSet {
  val laws: List<Law>
}

data class Law(val name: String, val test: suspend TestScope.() -> Unit)

fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { x, y -> x == y }): Boolean =
  if (f(this, b)) true else fail("Found $this but expected: $b")

fun testLaws(vararg lawSets: LawSet): TestResult = testLaws(lawSets.flatMap { it.laws })

fun testLaws(vararg laws: List<Law>): TestResult = runTest {
  laws
    .flatMap(List<Law>::asIterable)
    .distinctBy(Law::name)
    .forEach { law: Law ->
      withClue("Testing ${law.name}") {
        law.test(this@runTest)
      }
    }
}
