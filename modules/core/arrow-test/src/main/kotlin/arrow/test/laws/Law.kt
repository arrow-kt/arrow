package arrow.test.laws

import arrow.test.generators.genTuple
import arrow.typeclasses.Eq
import io.kotlintest.TestContext
import io.kotlintest.properties.Gen

data class Law(val name: String, val test: TestContext.() -> Unit)

fun <A> A.equalUnderTheLaw(b: A, eq: Eq<A>): Boolean =
  eq.run { eqv(b) }

fun <A> forFew(amount: Int, genA: Gen<A>, fn: (a: A) -> Boolean) {
  val listA = genA.random().take(amount).map(fn).toList()
  listA.forEachIndexed { index :Int ,passed:Boolean ->
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