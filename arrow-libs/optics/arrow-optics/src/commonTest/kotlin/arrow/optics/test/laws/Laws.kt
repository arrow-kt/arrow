package arrow.optics.test.laws

import io.kotest.assertions.fail
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.StringSpecScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestContext

public data class Law(val name: String, val test: suspend TestContext.() -> Unit)

public fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { a, b -> a == b }): Boolean =
  if (f(this, b)) true else fail("Found $this but expected: $b")

public fun StringSpec.testLaws(vararg laws: List<Law>): Unit = laws
  .flatMap { list: List<Law> -> list.asIterable() }
  .distinctBy { law: Law -> law.name }
  .forEach { law: Law ->
    addTest(TestName(null, law.name, false), false, null) {
      law.test(StringSpecScope(this.coroutineContext, testCase))
    }
  }

public fun StringSpec.testLaws(prefix: String, vararg laws: List<Law>): Unit = laws
  .flatMap { list: List<Law> -> list.asIterable() }
  .distinctBy { law: Law -> law.name }
  .forEach { law: Law ->
    addTest(TestName(prefix, law.name, false), false, null) {
      law.test(StringSpecScope(this.coroutineContext, testCase))
    }
  }
