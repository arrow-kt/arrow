package arrow.test.laws

import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqLaws {

    inline fun <reified F> laws(EQ: Eq<F>, noinline cf: (Int) -> F): List<Law> =
            listOf(
                    Law("Eq Laws: identity", { EQ.identityEquality(cf) }),
                    Law("Eq Laws: commutativity", { EQ.commutativeEquality(cf) })
            )

    inline fun <reified F> Eq<F>.identityEquality(crossinline cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                a.eqv(a) == a.eqv(a)
            })

    inline fun <reified F> Eq<F>.commutativeEquality(crossinline cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                val b = cf(int)
                a.eqv(b) == b.eqv(a)
            })
}