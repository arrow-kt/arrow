package arrow.test.laws

import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqLaws {

    inline fun <F> laws(EQ: Eq<F>, noinline cf: (Int) -> F): List<Law> =
            listOf(
                    Law("Eq Laws: identity", { EQ.identityEquality(cf) }),
                    Law("Eq Laws: commutativity", { EQ.commutativeEquality(cf) })
            )

    fun <F> Eq<F>.identityEquality(cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                a.eqv(a) == a.eqv(a)
            })

    fun <F> Eq<F>.commutativeEquality(cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                val b = cf(int)
                a.eqv(b) == b.eqv(a)
            })
}