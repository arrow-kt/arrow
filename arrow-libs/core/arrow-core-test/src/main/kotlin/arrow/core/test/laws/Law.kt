package arrow.core.test.laws

import arrow.typeclasses.Eq
import io.kotlintest.TestContext

data class Law(val name: String, val test: suspend TestContext.() -> Unit)

fun <A> A.equalUnderTheLaw(b: A, eq: Eq<A>): Boolean =
  eq.run { eqv(b) }
