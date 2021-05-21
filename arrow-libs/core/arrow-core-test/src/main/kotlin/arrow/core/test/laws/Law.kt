package arrow.core.test.laws

import io.kotest.assertions.fail
import io.kotest.core.test.TestContext

data class Law(val name: String, val test: suspend TestContext.() -> Unit)

fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { a, b -> a == b }): Boolean =
  if(f(this, b)) true else fail("Found $this but expected: $b")
