package arrow.core.test.laws

import io.kotlintest.TestContext

data class Law(val name: String, val test: suspend TestContext.() -> Unit)

fun <A> A.equalUnderTheLaw(b: A, f: (A, A) -> Boolean = { a, b -> a?.equals(b) == true }): Boolean =
  f(this, b)
