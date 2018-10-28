package arrow.test.laws

import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqLaws {

  fun <F> laws(EQ: Eq<F>, cf: (Int) -> F): List<Law> =
    listOf(
      Law("Eq Laws: reflexivity") { EQ.reflexivityEquality(cf) },
      Law("Eq Laws: commutativity") { EQ.commutativeEquality(cf) },
      Law("Eq Laws: transitivity") { EQ.transitiveEquality(cf) }
    )

  fun <F> Eq<F>.reflexivityEquality(cf: (Int) -> F): Unit =
    forAll(Gen.int()) { int: Int ->
      val a = cf(int)
      a.eqv(a)
    }

  fun <F> Eq<F>.commutativeEquality(cf: (Int) -> F): Unit =
    forAll(Gen.int()) { int: Int ->
      val a = cf(int)
      val b = cf(int)
      a.eqv(b) == b.eqv(a)
    }

  fun <F> Eq<F>.transitiveEquality(cf: (Int) -> F): Unit =
    forAll(Gen.int()) { int: Int ->
      val a = cf(int)
      val b = cf(int)
      val c = cf(int)
      !(a.eqv(b) && b.eqv(c)) || a.eqv(c)
    }
}