package arrow.test.laws

import arrow.typeclasses.*
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object HashLaws {

    inline fun <reified F> laws(H: Hash<F> = hash<F>(), EQ: Eq<F> = eq<F>(), noinline cf: (Int) -> F): List<Law> =
            EqLaws.laws(EQ, cf) + listOf(
                    Law("Hash Laws: equality", { equalHash(H, EQ, cf) })
            )

    inline fun <reified F> equalHash(H: Hash<F> = hash(), EQ: Eq<F> = eq(), crossinline cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                val b = cf(int)
                EQ.eqv(a, b) && H.hash(a) == H.hash(b)
            })

}
