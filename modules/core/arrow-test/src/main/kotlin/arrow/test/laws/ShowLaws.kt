package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import arrow.typeclasses.eq
import arrow.typeclasses.show
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ShowLaws {

    inline fun <reified F> laws(S: Show<F> = show<F>(), EQ: Eq<F> = eq<F>(), noinline cf: (Int) -> F): List<Law> =
            EqLaws.laws(EQ, cf) + listOf(
                    Law("Show Laws: equality", { equalShow(S, EQ, cf) })
            )

    inline fun <reified F> equalShow(S: Show<F> = show(), EQ: Eq<F> = eq(), crossinline cf: (Int) -> F): Unit =
            forAll(Gen.int(), { int: Int ->
                val a = cf(int)
                val b = cf(int)
                EQ.eqv(a, b) && S.show(a) == S.show(b)
            })

}