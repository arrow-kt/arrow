package arrow.core.test.laws

import arrow.core.test.concurrency.deprecateArrowTestModules
import io.kotest.assertions.fail
import io.kotest.core.test.TestContext

@Deprecated(deprecateArrowTestModules)
public data class Law(val name: String, val test: suspend TestContext.() -> Unit)

@Deprecated(deprecateArrowTestModules)
public fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { a, b -> a == b }): Boolean =
  if (f(this, b)) true else fail("Found $this but expected: $b")
