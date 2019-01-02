package arrow.test.laws

import arrow.test.generators.genTuple
import arrow.typeclasses.Eq
import io.kotlintest.should
import io.kotlintest.properties.Gen

fun throwableEq() = Eq { a: Throwable, b ->
  a::class == b::class && a.message == b.message
}

data class Law(val name: String, val test: () -> Unit)

fun <A> A.equalUnderTheLaw(b: A, eq: Eq<A>): Boolean =
  eq.run { eqv(b) }

fun <A> A.shouldBe(b: A, eq: Eq<A>): Unit = eq.run {
  this.should {
    io.kotlintest.matchers.Result(eqv(b),
      "Expected: $this but found: $b")
  }
}

fun <A> forFew(amount: Int, gena: Gen<A>, fn: (a: A) -> Boolean): Unit {
  for (k in 0..amount) {
    val a = gena.generate()
    val passed = fn(a)
    if (!passed) {
      throw AssertionError("Property failed for\n$a)")
    }
  }
}

fun <A, B> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean): Unit {
  for (k in 0..amount) {
    val a = gena.generate()
    val b = genb.generate()
    val passed = fn(a, b)
    if (!passed) {
      throw AssertionError("Property failed for\n${listA[index]})")
    }
  }
}

fun <A, B> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  genTuple(gena, genb).random().take(amount).map {
    if (!fn(it.a, it.b)) {
      throw AssertionError("Property failed for\n${it.a}\n${it.b})")
    }
  }
}

fun <A, B, C> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
  genTuple(gena, genb, genc).random().take(amount).map {
    if (!fn(it.a, it.b, it.c)) {
      throw AssertionError("Property failed for\n${it.a}\n${it.b}\n${it.c})")
    }
  }
}

fun <A, B, C, D> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
  genTuple(gena, genb, genc, gend).random().take(amount).map {
    if (!fn(it.a, it.b, it.c, it.d)) {
      throw AssertionError("Property failed for\n${it.a}\n${it.b}\n${it.c}\n${it.d})")
    }
  }
}

fun <A, B, C, D, E> forFew(amount: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
  genTuple(gena, genb, genc, gend, gene).random().take(amount).map {
    if (!fn(it.a, it.b, it.c, it.d, it.e)) {
      throw AssertionError("Property failed for\n${it.a}\n${it.b}\n${it.c}\n${it.d}\n${it.e})")
    }
  }
}
