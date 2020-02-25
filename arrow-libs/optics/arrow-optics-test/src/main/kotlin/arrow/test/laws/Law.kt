package arrow.test.laws

import arrow.test.generators.tuple2
import arrow.typeclasses.Eq
import io.kotlintest.TestContext
import io.kotlintest.properties.Gen

fun throwableEq() = Eq { a: Throwable, b ->
  a::class == b::class && a.message == b.message
}

data class Law(val name: String, val test: suspend TestContext.() -> Unit)

fun <A> A.equalUnderTheLaw(b: A, eq: Eq<A>): Boolean =
  eq.run { eqv(b) }

fun <A> forFew(amount: Int, gena: Gen<A>, fn: (a: A) -> Boolean) {
  gena.random().take(amount).toList().map {
    if (!fn(it)) {
      throw AssertionError("Property failed for\n$it)")
    }
  }
}

fun <A, B> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  Gen.tuple2(gena, genb).random().take(amount).toList().map {
    if (!fn(it.a, it.b)) {
      throw AssertionError("Property failed for\n${it.a}\n${it.b})")
    }
  }
}
