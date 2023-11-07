package arrow.core.test

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.addTest
import kotlinx.coroutines.test.runTest

interface LawSet {
  val laws: List<Law>
}

data class Law(val name: String, val test: suspend () -> Unit)

fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { x, y -> x == y }): Boolean =
  if (f(this, b)) true else fail("Found $this but expected: $b")

fun StringSpec.testLaws(lawSet: LawSet): Unit = testLaws(lawSet.laws)

fun StringSpec.testLaws(vararg laws: List<Law>): Unit = laws
  .flatMap { list: List<Law> -> list.asIterable() }
  .distinctBy { law: Law -> law.name }
  .forEach { law: Law ->
    addTest(TestName(null, law.name, false), false, null) {
      runTest { law.test() }
    }
  }

fun testLawsCommon(lawSet: LawSet) = withClue("In $lawSet") {
  testLawsCommon(lawSet.laws)
}

fun testLawsCommon(vararg laws: List<Law>) = runTest {
  laws
    .flatMap(List<Law>::asIterable)
    .distinctBy(Law::name)
    .forEach { law: Law ->
      withClue("Testing ${law.name}") {
        law.test()
      }
    }
}
